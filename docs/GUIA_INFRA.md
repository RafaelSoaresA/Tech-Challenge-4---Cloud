# Guia do Dev 4: Infraestrutura AWS e CI/CD (US10, US11, US12)

Guia para quem nunca usou AWS. Siga as etapas na ordem. Cada uma termina com um teste rápido para você confirmar que deu certo.

---

## 0. Como a conta gratuita funciona (leia antes de criar)

- Contas criadas desde 15/07/2025 escolhem entre **Free Plan** e **Paid Plan** no cadastro. Escolha **Free Plan**.
- Você recebe **US$ 100 em créditos** na criação e pode ganhar **mais US$ 100** fazendo atividades guiadas no console (uma delas é criar um Budget, outra é usar Lambda).
- No Free Plan **você nunca é cobrado**. Quando passam **6 meses** ou acabam os créditos, a conta é **fechada** (você tem 90 dias para fazer upgrade e recuperar). Para um Tech Challenge isso é suficiente.
- **Não entre em AWS Organizations** com essa conta: isso converte para Paid na hora.
- Os serviços deste projeto (Lambda, DynamoDB, API Gateway, SNS, EventBridge Scheduler, S3, CloudWatch) custam **centavos** no volume de um projeto acadêmico. Os créditos sobram com folga.
- O que realmente gasta crédito e **não** usamos: EC2 ligado 24h, NAT Gateway, RDS, Lambda dentro de VPC. Se algum tutorial pedir isso, ignore.

---

## 1. Criar e proteger a conta (30 min) — parte da US10

1. Acesse https://aws.amazon.com/free e clique em **Create a Free Account**. Use um e-mail da equipe (ex. um Gmail criado para o projeto), assim ninguém depende da sua conta pessoal.
2. Escolha **Free Plan**. Será pedido um cartão (verificação) e um telefone.
3. Ao entrar, você está logado como **root** (o "dono"). Faça só isto com ele:
   - **Ativar MFA no root**: canto superior direito → nome da conta → *Security credentials* → *Assign MFA device* → app autenticador (Google Authenticator, Authy).
   - **Criar um Budget**: busque *Budgets* na barra de pesquisa → *Create budget* → *Use a template* → **Zero spend budget** → seu e-mail. Você será avisado se qualquer centavo for gasto além dos créditos. (Isso também conta como atividade de crédito extra.)
4. **Criar um usuário para o dia a dia** (não use root para trabalhar):
   - Busque **IAM** → *Users* → *Create user* → nome `dev4-rafael` → marque *Provide user access to the AWS Management Console*.
   - Permissões: *Attach policies directly* → `AdministratorAccess` (só para você, que é o dono da infra; o Dev 5 refina depois).
   - Ative MFA neste usuário também.
   - Guarde o link de login que aparece (formato `https://<id-da-conta>.signin.aws.amazon.com/console`).
5. **Região**: no canto superior direito, selecione **US East (N. Virginia) us-east-1** e use sempre ela. É a região mais barata e com todos os serviços. Recursos criados em outra região "somem" da tela; se algo não aparecer, confira a região primeiro.

**Teste:** saia do root e entre com o usuário IAM pelo link guardado.

**Colegas (Devs 1, 2, 3, 5):** crie um usuário IAM para cada um, num grupo `devs` com acesso de leitura (`ReadOnlyAccess`) + `CloudWatchLogsReadOnlyAccess`. Eles não precisam fazer deploy manual: o deploy é feito pelo GitHub Actions. O Dev 5 pode receber mais permissões para trabalhar em segurança/monitoramento.

---

## 2. Entendendo cada peça da arquitetura

Pense na AWS como um conjunto de peças de Lego gerenciadas. Você não instala servidores; você declara quais peças quer e como se conectam.

| Peça | O que é, em uma frase | Papel no projeto | Equivalente no Azure (desenho da equipe) |
|---|---|---|---|
| **API Gateway (HTTP API)** | A "porta de entrada" HTTPS pública. Recebe a requisição e repassa para uma Lambda. | Expõe `POST /avaliacao` | Gatilho HTTP da Function |
| **Lambda** | Uma função (aqui, código Java/Quarkus) que roda só quando é chamada. Você paga por milissegundo de execução. Sem servidor ligado. | 3 funções: receber, alertar, relatório | Azure Functions |
| **DynamoDB** | Banco NoSQL gerenciado. Uma tabela com chave `id`. Sem servidor, sem senha. | Guarda as avaliações | Cosmos DB |
| **DynamoDB Streams** | Uma "fila de eventos" automática: cada item inserido na tabela vira um evento. | Dispara a Lambda de alerta (é o "evento" da US03) | Change Feed |
| **SNS** | Serviço de notificação: você publica uma mensagem num *tópico*, e todos os *assinantes* (e-mails) recebem. | E-mail de avaliação crítica | Communication Services |
| **EventBridge Scheduler** | Um "cron" gerenciado: chama algo em um horário definido. | Toda segunda às 8h chama a Lambda de relatório | Timer Trigger |
| **S3** | Armazenamento de arquivos (buckets). | Guarda o relatório JSON | Blob Storage |
| **CloudWatch** | Logs e métricas de tudo. Todo `print()` de uma Lambda aparece aqui. | Observabilidade (Dev 5) | Application Insights |
| **IAM** | Quem pode fazer o quê. Cada Lambda recebe uma *role* com permissão mínima. | Segurança (Dev 5, mas o template já cria as roles) | Managed Identity / RBAC |
| **CloudFormation** | O motor da AWS que cria recursos a partir de um arquivo YAML. Um conjunto de recursos é uma *stack*. | O SAM usa ele por baixo | ARM / Bicep |
| **AWS SAM** | Um "atalho" para CloudFormation feito para serverless: com poucas linhas você declara Lambda + gatilho + permissões. | Nossa ferramenta de IaC (US11) | — |

**Fluxo completo:**
`Aluno/Postman → API Gateway → Lambda receber → DynamoDB → (Stream) → Lambda alerta → SNS → e-mail`
`EventBridge Scheduler (segunda 8h) → Lambda relatório → lê DynamoDB → grava JSON no S3`

### Por que SAM e não Terraform?
O PDF aceita os dois. Para quem está começando, o SAM é mais simples:
- **Não precisa de "state file"**. O Terraform exige guardar o estado num bucket S3 + controle de lock, e configurar isso no CI é a parte que mais dá erro. O SAM guarda o estado no próprio CloudFormation.
- Gatilhos (HTTP, Stream, agendamento) e permissões são **uma linha cada**.
- Se a stack der erro no meio, o CloudFormation **desfaz sozinho** (rollback).

---

## 3. Preparar sua máquina (20 min)

Instale:
1. **AWS CLI v2** — https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
2. **AWS SAM CLI** — https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html
3. **JDK 21** (ex. Temurin) e **Maven 3.9+**, os mesmos que a equipe já usa para o Quarkus. Confira com `java -version` e `mvn -v`.
4. **Git**.

Configure as credenciais do seu usuário IAM:
- No console: IAM → Users → `dev4-rafael` → *Security credentials* → *Create access key* → *Command Line Interface (CLI)*.
- No terminal:
  ```bash
  aws configure
  # Access key / Secret key: os que você acabou de gerar
  # Default region: us-east-1
  # Output format: json
  ```
- **Nunca** coloque essas chaves no GitHub. Elas são só da sua máquina.

**Teste:** `aws sts get-caller-identity` deve mostrar seu usuário.

---

## 4. US10 + US11 — Provisionar o ambiente com SAM

Estrutura do repositório (já está neste kit):

```
pom.xml                  ← projeto Maven pai (versão do Quarkus e módulos)
template.yaml            ← TODA a infraestrutura (US10/US11)
samconfig.toml           ← configurações do deploy
receber-avaliacao/       ← Lambda 1, módulo Quarkus (Dev 1)
notificar-critica/       ← Lambda 2, módulo Quarkus (Dev 2)
relatorio-semanal/       ← Lambda 3, módulo Quarkus (Dev 3)
.github/workflows/       ← pipeline (US12)
bootstrap/               ← role do GitHub na AWS (rodar uma vez)
```

Como o Java chega na AWS: cada módulo, ao rodar `mvn package`, gera um `target/function.zip` (é a extensão `quarkus-amazon-lambda` que faz isso). O `template.yaml` aponta o `CodeUri` de cada Lambda para esse zip, e o `sam deploy` sobe os três. **Não se usa `sam build`**, porque o Maven já fez o empacotamento.

O que o `template.yaml` cria (checklist da US10):
- [x] API Gateway → evento `HttpApi` na Lambda 1
- [x] 3 funções Lambda (Java 21, 512 MB, arm64; Java precisa de mais memória que Python para iniciar rápido)
- [x] Tabela DynamoDB `avaliacoes-dev` com Stream ligado
- [x] Tópico SNS + assinatura de e-mail do administrador
- [x] EventBridge Scheduler semanal → evento `ScheduleV2` na Lambda 3
- [x] Variáveis de ambiente (`TABELA_AVALIACOES`, `TOPICO_ALERTAS_ARN`, `BUCKET_RELATORIOS`, `LIMITE_NOTA_CRITICA`)
- [x] Bucket S3 privado para relatórios
- [x] Logs com retenção de 7 dias (evita custo acumulado)
- [x] Uma role IAM por Lambda, só com a permissão que ela precisa

### Primeiro deploy, manual, da sua máquina
1. Edite `samconfig.toml` e troque `SEU_EMAIL@exemplo.com` pelo e-mail do administrador.
2. Rode, na raiz do repositório:
   ```bash
   mvn clean package       # compila, roda os testes e gera o function.zip de cada Lambda
   sam validate --lint     # confere o template
   sam deploy              # sobe os zips e cria tudo; mostra o que vai criar e pede "y"
   ```
   Se o `sam deploy` reclamar que não encontra `target/function.zip`, o `mvn package` falhou ou não rodou. Confira se existe `receber-avaliacao/target/function.zip` (e os outros dois).
3. No final aparecem os **Outputs**, incluindo `ApiUrl`.
4. Você vai receber um e-mail **"AWS Notification - Subscription Confirmation"**. Clique em *Confirm subscription*. Sem isso, o SNS não envia alertas.

**Teste ponta a ponta:**
```bash
# avaliação normal → 201
curl -X POST "$API_URL/avaliacao" -H "Content-Type: application/json" \
  -d '{"descricao":"Aula excelente","nota":9}'

# avaliação crítica → 201 + e-mail chega em ~1 minuto
curl -X POST "$API_URL/avaliacao" -H "Content-Type: application/json" \
  -d '{"descricao":"Não consegui acompanhar","nota":2}'

# nota inválida → 400
curl -X POST "$API_URL/avaliacao" -H "Content-Type: application/json" \
  -d '{"descricao":"x","nota":15}'

# relatório sem esperar a segunda-feira:
sam remote invoke RelatorioSemanalFunction --stack-name feedback-aulas-dev
```
A primeira chamada de cada Lambda Java demora alguns segundos (cold start do Quarkus); as seguintes são rápidas.
Depois confira no console: **DynamoDB** → Tables → `avaliacoes-dev` → *Explore items*; **S3** → bucket de relatórios; **CloudWatch** → *Log groups* → `/aws/lambda/...`.

Onde ver a stack: console → **CloudFormation** → `feedback-aulas-dev` → abas *Resources* (tudo que foi criado), *Events* (histórico e erros), *Outputs*.

> As 3 Lambdas deste kit são **esqueletos funcionais** para você validar a infra sem depender dos colegas.

### 4.1 Encaixando o código real da equipe (Quarkus)
O template precisa saber três coisas de cada Lambda dos colegas. Preencha esta tabela com eles:

| O que o template precisa | Onde está no `template.yaml` | O que perguntar ao dev |
|---|---|---|
| Pasta do módulo | `CodeUri: <pasta>/target/function.zip` | Qual a pasta do módulo Maven? |
| Tipo de handler | `Handler` (está em `Globals`) e `QUARKUS_LAMBDA_HANDLER` | Usa `RequestHandler` com `@Named("...")` ou endpoint REST (JAX-RS)? |
| Variáveis de ambiente | `Environment.Variables` | Quais nomes de variável o código lê? |

Regras do Quarkus em Lambda:
- **Handler:** sempre `io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest`. Não troque.
- **Lambda com classe `RequestHandler`** (Devs 2 e 3 neste kit): o nome em `@Named("x")` deve ser igual a `QUARKUS_LAMBDA_HANDLER: x` no template (ou à propriedade `quarkus.lambda.handler`).
- **Lambda com endpoint REST** (Dev 1 neste kit): use a extensão `quarkus-amazon-lambda-http` (API Gateway HTTP API, que é o que o template cria). Se o dev usou `quarkus-amazon-lambda-rest`, ele espera o API Gateway REST (v1) e o template precisa trocar `HttpApi` por `Api`. Combinem isso cedo.
- **Dependência obrigatória** em cada módulo: `quarkus-amazon-lambda` (ou `-http`). Sem ela não sai o `function.zip`.
- **Variáveis de ambiente** viram propriedades do Quarkus: `TABELA_AVALIACOES` → `tabela.avaliacoes`. No kit, o `application.properties` já faz `tabela.avaliacoes=${TABELA_AVALIACOES:avaliacoes-dev}`.
- **Versão do Quarkus:** está no `pom.xml` raiz (`quarkus.platform.version`). Use a mesma do projeto da equipe.
- **DynamoDB/SNS/S3:** o kit usa as extensões `quarkus-amazon-dynamodb`, `-sns` e `-s3` com o cliente `url-connection-client` (leve, sem Netty nem Apache), o que deixa o zip menor e o cold start mais rápido. Se a equipe usar o AWS SDK direto, também funciona; o que importa é a Lambda ter as permissões que o template já concede.

Se a equipe tiver um repositório por dev, veja a seção sobre repositório único na conversa: cada módulo vira uma pasta e o `pom.xml` raiz lista todos em `<modules>`.

---

## 5. US12 — CI/CD com GitHub Actions

### 5.1 Ligar GitHub e AWS sem guardar senhas (OIDC) — uma vez só
Em vez de colocar access keys no GitHub, a AWS passa a "confiar" no GitHub Actions do **seu repositório, branch main**. É a forma recomendada e o Dev 5 vai gostar (US13).

1. Console → **CloudFormation** → *Create stack* → *With new resources* → *Upload a template file* → envie `bootstrap/github-oidc-role.yaml`.
2. Nome da stack: `github-oidc`. Parâmetros: `GitHubOwner` (usuário/organização) e `GitHubRepo` (nome do repositório).
3. Marque *I acknowledge that AWS CloudFormation might create IAM resources* → *Submit*.
4. Quando ficar `CREATE_COMPLETE`, vá em *Outputs* e copie o **RoleArn**.

> Se der erro dizendo que o provedor OIDC já existe, é porque já foi criado antes na conta; remova o recurso `GitHubOidcProvider` do arquivo e use o ARN existente.

### 5.2 Secrets no GitHub
Repositório → *Settings* → *Secrets and variables* → *Actions* → *New repository secret*:
- `AWS_DEPLOY_ROLE_ARN` = o RoleArn copiado
- `ADMIN_EMAIL` = e-mail que recebe alertas

### 5.3 Como o pipeline funciona (`.github/workflows/ci-cd.yml`)
| Evento | O que roda |
|---|---|
| Abrir/atualizar **Pull Request** para main | `testes`: `mvn verify` (compila, roda testes e gera os zips) + cfn-lint. Não toca na AWS. |
| **Merge/push na main** | `testes` → `deploy`: login OIDC → `mvn package` → `sam deploy` → **smoke test** (POST válido espera 201, nota inválida espera 400) |
| Botão *Run workflow* | O mesmo que push na main |

Checklist da US12:
- [x] Workflow no GitHub Actions
- [x] Testes automatizados antes do deploy (deploy só roda se os testes passarem)
- [x] Deploy das funções
- [x] Validação pós-deploy (smoke test)

**Proteja a main:** *Settings* → *Branches* → *Add rule* → `main` → marque *Require a pull request before merging* e *Require status checks to pass* (selecione `testes`). Isso atende o "revisado via Pull Request" do Definition of Done.

**Teste:** faça um PR qualquer, veja o check verde, faça merge e acompanhe em *Actions*.

---

## 6. Contratos que você precisa combinar com a equipe

| Com quem | O que combinar | Já definido no kit |
|---|---|---|
| Dev 1 | Nome da tabela, chave `id`, campos `descricao`, `nota`, `dataEnvio` (ISO 8601) | Sim, ajustem juntos se mudar |
| Dev 1 + Dev 2 | Evento de avaliação (US03): o DynamoDB Stream é o evento; o Dev 1 não chama o Dev 2 diretamente (desacoplado) | Sim |
| Dev 2 | Regra de criticidade: `nota <= LIMITE_NOTA_CRITICA` (padrão 4) — é um parâmetro do template | Sim |
| Dev 3 | Horário (segunda 8h, America/Sao_Paulo), formato JSON no S3 em `relatorios/AAAA-MM-DD.json` | Sim |
| Dev 5 | Refinar a política da role do GitHub, alarmes do CloudWatch (dá para adicionar no `template.yaml`) | Base pronta |

Regra de ouro: **ninguém cria recurso pelo console** (exceto o bootstrap). Tudo que for novo entra no `template.yaml` via PR. Senão a infra deixa de ser reproduzível (US11).

---

## 7. Custos esperados e cuidados

| Serviço | Uso no projeto | Custo estimado |
|---|---|---|
| Lambda (Java, 512 MB) | centenas de execuções | US$ 0 (1 milhão de requisições e 400.000 GB-s por mês são sempre gratuitos) |
| DynamoDB | algumas centenas de itens | < US$ 0,01 |
| API Gateway HTTP | centenas de chamadas | < US$ 0,01 |
| SNS e-mail | dezenas de e-mails | US$ 0 (1.000 e-mails/mês grátis) |
| EventBridge Scheduler | 4 execuções/mês | US$ 0 |
| S3 / CloudWatch | KB de dados | < US$ 0,10 |

Cuidados:
- Não crie EC2, RDS, NAT Gateway nem coloque Lambda em VPC.
- Não deixe scripts de teste em loop chamando a API.
- Depois da entrega e do vídeo, apague tudo: `sam delete --stack-name feedback-aulas-dev` (e a stack `github-oidc` no console). Esvazie o bucket S3 antes se o delete reclamar.

---

## 8. Problemas comuns

| Sintoma | Causa provável / solução |
|---|---|
| Recurso "sumiu" no console | Região errada. Volte para us-east-1. |
| `CREATE_FAILED` / `ROLLBACK_COMPLETE` | CloudFormation → stack → aba *Events*: a primeira linha vermelha diz o motivo. Se a stack ficar em `ROLLBACK_COMPLETE`, apague-a e faça deploy de novo. |
| E-mail de alerta não chega | Assinatura SNS não confirmada (veja o spam também). |
| `{"message":"Not Found"}` na API | Caminho errado: é `/avaliacao`, sem "ç", com POST. |
| `sam deploy` não acha `target/function.zip` | O `mvn package` não rodou ou falhou. Rode `mvn clean package` na raiz e veja o erro. Sem a dependência `quarkus-amazon-lambda` (ou `-http`) no módulo, o zip não é gerado. |
| Lambda falha com `Unable to find handler` / `No handler found` | `QUARKUS_LAMBDA_HANDLER` diferente do `@Named("...")` da classe, ou mais de um `RequestHandler` no módulo sem definir qual usar. |
| 1ª chamada lenta (3 a 8 s) | Cold start normal do Java. Se incomodar na demonstração, chame a API uma vez antes de gravar. O `template.yaml` já usa 512 MB e arm64; a opção seguinte é SnapStart. |
| `Task timed out` no API Gateway (503/504) | Timeout da Lambda (30 s no template). Veja no CloudWatch se foi o cold start ou uma chamada travada ao DynamoDB. |
| 500 na API | CloudWatch → log group da Lambda → a exceção Java (stack trace) estará lá. |
| GitHub Actions: `Not authorized to perform sts:AssumeRoleWithWebIdentity` | Nome do owner/repo no bootstrap diferente do real (maiúsculas contam), ou o deploy rodou fora da main. |
| `AccessDenied` no deploy pelo GitHub | A role do bootstrap não tem a permissão; o nome do erro diz qual ação adicionar. |

---

## 9. Roteiro sugerido por sprint

- **Sprint 1:** etapas 0 a 4 (conta, máquina, primeiro `sam deploy` manual). Entregue o `ApiUrl` para a equipe.
- **Sprint 2:** etapa 5 (OIDC + pipeline + proteção da main). Colegas passam a entregar via PR.
- **Sprint 3:** apoiar integração, gravar no vídeo: stack no CloudFormation, pipeline verde, Lambdas no console, e-mail chegando, relatório no S3.
