# Dev 5 — Segurança, observabilidade e qualidade (US13, US14, US15)

Responsável: Vinicius. Frente transversal: não cria feature de negócio; garante segurança, observabilidade e qualidade da entrega.

Conta AWS do grupo: `629137454819` (`Tech Challenge 4 - devOps`), região `us-east-1`.

## Arquitetura

```
Aluno/Postman
    → API Gateway (HTTPS)
        → Lambda receber-avaliacao-dev (Java 21 / Quarkus)
            → DynamoDB avaliacoes-dev (id, descricao, nota, dataEnvio)
                → DynamoDB Stream (evento US03)
                    → Lambda notificar-critica-dev
                        → SNS → e-mail (descrição, urgência, data de envio)
EventBridge (segunda 8h, America/Sao_Paulo)
    → Lambda relatorio-semanal-dev
        → DynamoDB → S3 (bucket privado)
```

Stack CloudFormation/SAM: `feedback-aulas-dev`. Modelo: AWS Serverless. Sem EC2/VPC. Lambdas usam execution role (sem access key no código).

## US13 — Segurança Cloud

| Controle | Situação |
|---|---|
| IAM com menor privilégio nas Lambdas | Roles geradas pelo SAM: receber só grava na tabela; notificar só publica no SNS; relatório só lê DynamoDB e grava no S3 |
| DynamoDB / S3 restritos | Policies do SAM amarradas ao recurso concreto |
| Segredos / deploy | GitHub Actions via OIDC (`bootstrap/github-oidc-role.yaml`) — sem access key no repositório |
| Bucket de relatórios | Bloqueio de acesso público + lifecycle 90 dias |
| Usuários IAM individuais | `vinicius`, `dev4-Rafael`, `joao`, `felipe`, `gabriel` (acesso ao console habilitado) |
| MFA | Ativo em `vinicius`, `dev4-Rafael` e `joao`. Credenciais enviadas a Felipe e Gabriel; MFA deles é self-service |

O que **não** fazer: credencial em chat/README/commit; usar root no dia a dia; abrir o bucket S3.

## US14 — Observabilidade (já na conta AWS)

Recursos criados no console (não versionados de novo no `template.yaml` para evitar duplicar):

| Recurso | Nome |
|---|---|
| Log groups | `/aws/lambda/receber-avaliacao-dev`, `.../notificar-critica-dev`, `.../relatorio-semanal-dev` (retenção 7 dias no SAM) |
| Alarmes Errors (≥ 1 em 5 min) | `receber-avaliacao-dev-errors`, `notificar-critica-dev-errors`, `relatorio-semanal-dev-errors` |
| Dashboard | `feedback-aulas-dev-observabilidade` (Errors + Invocations + Duration × 3 Lambdas) |

### Como validar no console

1. Região **us-east-1** → CloudWatch → *Log groups* → filtrar `Avaliacao registrada` / `Alerta enviado`.
2. CloudWatch → *Alarms* → os três `*-errors` acima.
3. CloudWatch → *Dashboards* → `feedback-aulas-dev-observabilidade`.

## US15 — Qualidade e documentação

| Entrega | Evidência |
|---|---|
| Contrato entre serviços | Item DynamoDB: `id` (S), `descricao` (S), `nota` (N), `dataEnvio` (S ISO-8601). Criticidade: `nota <= LimiteNotaCritica` (padrão 4). Evento = DynamoDB Stream |
| Testes de contrato | `ItemAvaliacaoDynamoTest` (receber); `ContratoEventoAvaliacaoTest` (notificar); testes unitários dos módulos; smoke test do CI (`201` / `400`) |
| Documentação | Este arquivo + [`GUIA_INFRA.md`](GUIA_INFRA.md) + [`ROTEIRO_DEMONSTRACAO.md`](ROTEIRO_DEMONSTRACAO.md) + README |
| Deploy | `mvn verify` / `sam deploy` documentados no README |

```bash
mvn -B verify
```

## Critérios FIAP (vídeo / README)

| Critério | Onde mostrar |
|---|---|
| Modelo de cloud | Diagrama acima + stack `feedback-aulas-dev` |
| Segurança | Roles das Lambdas + OIDC + usuários IAM |
| Monitoramento | Log groups + 3 alarmes + dashboard |
| Qualidade / docs | Actions verde + este guia + roteiro de demo |
