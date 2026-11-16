# Tech Challenge Fase 4 — Plataforma de feedbacks de aulas (AWS, Java)

Infraestrutura como código com **AWS SAM**. A API de avaliação é **Spring Boot** no Elastic Beanstalk. As funções de alerta e de relatório são **Lambdas Java 21 + Quarkus**. O deploy é automático com **GitHub Actions**.

- Guia de infraestrutura e deploy: [`docs/GUIA_INFRA.md`](docs/GUIA_INFRA.md)
- Segurança, monitoramento e qualidade (Dev 5): [`docs/SEGURANCA_E_MONITORAMENTO.md`](docs/SEGURANCA_E_MONITORAMENTO.md)
- Roteiro do vídeo de demonstração: [`docs/ROTEIRO_DEMONSTRACAO.md`](docs/ROTEIRO_DEMONSTRACAO.md)
- Infra: [`template.yaml`](template.yaml)
- Pipeline: [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml)

## Módulos

| Pasta | O que sobe | Responsável |
|---|---|---|
| `avaliacao-api/` | API Spring no Elastic Beanstalk. `POST /api/avaliacao/v1` grava no DynamoDB e publica no SNS `avaliacao-eventos` | Dev 1 |
| `notificar-critica/` | DynamoDB Stream → SNS `alertas-criticos` (e-mail) | Dev 2 |
| `relatorio-semanal/` | EventBridge Scheduler → S3 | Dev 3 |

Transversal (Dev 5): IAM mínimo nas roles, logs/alarmes CloudWatch, teste de contrato, documentação e roteiro de demo.

## Contrato do item (DynamoDB)

Campos gravados pela `avaliacao-api` e lidos pelas Lambdas:

| Campo | Tipo | Exemplo |
|---|---|---|
| `id` | S | UUID |
| `descricao` | S | texto do feedback |
| `nota` | N | `0` a `10` |
| `dataEnvio` | S | ISO-8601 (`2026-11-15T12:00:00Z`) |

Urgência do alerta: nota `<= 4` é alta; abaixo de `7` é média. As duas geram e-mail. A partir de `7` não gera.

## Deploy

O deploy da `main` é o GitHub Actions (OIDC). Ele empacota as Lambdas, envia o zip da API ao S3 e roda `sam deploy`.

## Testes

```bash
mvn verify
mvn -B -f avaliacao-api/pom.xml verify
```

O CI também valida o `template.yaml` com `cfn-lint` e, após o deploy na `main`, faz smoke test (`201` e `400`) em `POST /api/avaliacao/v1`.

## Remover tudo

```bash
sam delete --stack-name feedback-aulas-dev
```
