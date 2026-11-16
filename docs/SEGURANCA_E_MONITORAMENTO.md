# Dev 5 — Segurança, observabilidade e qualidade (US13, US14, US15)

Responsável: Vinicius. Frente transversal: não cria feature de negócio; garante segurança, observabilidade e qualidade da entrega.

Conta AWS do grupo: `629137454819` (`Tech Challenge 4 - devOps`), região `us-east-1`.

## Arquitetura

```
Aluno/Postman
    → API Gateway (HTTPS)  /api/avaliacao/v1
        → Elastic Beanstalk feedback-api-dev (Spring Boot, 1 EC2)
            → DynamoDB avaliacoes-dev (id, descricao, nota, dataEnvio)
            → SNS avaliacao-eventos-dev (evento AVALIACAO_CRIADA)
                DynamoDB Stream
                    → Lambda notificar-critica-dev
                        → SNS alertas-criticos-dev → e-mail
EventBridge (segunda 8h, America/Sao_Paulo)
    → Lambda relatorio-semanal-dev
        → DynamoDB → S3 (bucket privado)
```

Stack CloudFormation/SAM: `feedback-aulas-dev`. A API usa uma instância EC2 do Elastic Beanstalk. As outras duas funções continuam Lambda, cada uma com execution role (sem access key no código).

URL da API após o deploy de 16/11/2026: `https://ijmxx80oh3.execute-api.us-east-1.amazonaws.com`.

## US13 — Segurança Cloud

| Controle | Situação |
|---|---|
| IAM da API | Role da instância: DynamoDB só na tabela `avaliacoes-dev`; `sns:Publish` só no tópico `avaliacao-eventos-dev` |
| IAM das Lambdas | Notificar só publica em `alertas-criticos-dev`. Relatório só lê a tabela e grava no bucket de relatórios |
| DynamoDB / S3 restritos | Policies amarradas ao recurso concreto. Bucket sem acesso público e com lifecycle de 90 dias |
| Segredos / deploy | GitHub Actions via OIDC (`bootstrap/github-oidc-role.yaml`) — sem access key no repositório |
| Usuários IAM individuais | `vinicius`, `dev4-Rafael`, `joao`, `felipe`, `gabriel` (acesso ao console habilitado) |
| MFA | Ativo em `vinicius`, `dev4-Rafael` e `joao`. Credenciais enviadas a Felipe e Gabriel; MFA deles é self-service |

O que **não** fazer: credencial em chat/README/commit; usar root no dia a dia; abrir o bucket S3.

## US14 — Observabilidade

| Recurso | Nome |
|---|---|
| Logs da API | Elastic Beanstalk envia o log da instância ao CloudWatch (retenção 7 dias, no `template.yaml`) |
| Log groups das Lambdas | `/aws/lambda/notificar-critica-dev`, `/aws/lambda/relatorio-semanal-dev` (retenção 7 dias) |
| Alarmes Errors (≥ 1 em 5 min) | `notificar-critica-dev-errors`, `relatorio-semanal-dev-errors` |
| Dashboard | `feedback-aulas-dev-observabilidade` (Errors, Invocations e Duration das Lambdas) |

### Como validar no console

1. Região **us-east-1** → CloudWatch → *Log groups* da Lambda `notificar-critica-dev` → filtrar `Alerta enviado`.
2. CloudWatch → log do ambiente `feedback-api-dev` → filtrar `Avaliação salva com sucesso`.
3. CloudWatch → *Alarms* → os dois `*-errors` das Lambdas que ainda existem.
4. CloudWatch → *Dashboards* → `feedback-aulas-dev-observabilidade`.

## US15 — Qualidade e documentação

| Entrega | Evidência |
|---|---|
| Contrato entre serviços | Item DynamoDB: `id` (S), `descricao` (S), `nota` (N), `dataEnvio` (S ISO-8601). Evento para o alerta = DynamoDB Stream |
| Urgência | `<= 4` alta; `< 7` média; as duas disparam e-mail. `>= 7` não dispara |
| Testes | `ContratoEventoAvaliacaoTest` (notificar); testes da `avaliacao-api`; smoke do CI (`201` / `400` em `POST /api/avaliacao/v1`) |
| Documentação | Este arquivo + [`ROTEIRO_DEMONSTRACAO.md`](ROTEIRO_DEMONSTRACAO.md) + README |

```bash
mvn -B verify
mvn -B -f avaliacao-api/pom.xml verify
```

## Critérios FIAP (vídeo / README)

| Critério | Onde mostrar |
|---|---|
| Modelo de cloud | Diagrama acima + stack `feedback-aulas-dev` |
| Segurança | Role da instância, roles das Lambdas, OIDC e usuários IAM |
| Monitoramento | Logs do Beanstalk e das Lambdas, alarmes e dashboard |
| Qualidade / docs | Actions verde + este guia + roteiro de demo |
