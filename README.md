# Tech Challenge Fase 4 — Plataforma de feedbacks de aulas (AWS Serverless, Java/Quarkus)

Infraestrutura como código com **AWS SAM**, Lambdas em **Java 21 + Quarkus** (Maven) e deploy automático com **GitHub Actions**.

- Guia completo de infraestrutura e deploy: [`docs/GUIA_INFRA.md`](docs/GUIA_INFRA.md)
- Infra: [`template.yaml`](template.yaml)
- Pipeline: [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml)

## Módulos (uma Lambda cada)
| Pasta | Lambda | Responsável |
|---|---|---|
| `receber-avaliacao/` | `POST /avaliacao` → DynamoDB | Dev 1 |
| `notificar-critica/` | DynamoDB Stream → SNS (e-mail) | Dev 2 |
| `relatorio-semanal/` | EventBridge Scheduler → S3 | Dev 3 |

## Deploy rápido (máquina local)
Requisitos: JDK 21, Maven, AWS CLI e SAM CLI.
```bash
mvn clean package        # gera target/function.zip em cada módulo
sam deploy               # ajuste AdminEmail em samconfig.toml antes
```

## Testes
```bash
mvn verify
```

## Remover tudo
```bash
sam delete --stack-name feedback-aulas-dev
```
