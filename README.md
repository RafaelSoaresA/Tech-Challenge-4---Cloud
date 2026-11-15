# Tech Challenge Fase 4 — Plataforma de feedbacks de aulas (AWS Serverless, Java/Quarkus)

Infraestrutura como código com **AWS SAM**, Lambdas em **Java 21 + Quarkus** (Maven) e deploy automático com **GitHub Actions**.

- Guia de infraestrutura e deploy: [`docs/GUIA_INFRA.md`](docs/GUIA_INFRA.md)
- Segurança, monitoramento e qualidade (Dev 5): [`docs/SEGURANCA_E_MONITORAMENTO.md`](docs/SEGURANCA_E_MONITORAMENTO.md)
- Roteiro do vídeo de demonstração: [`docs/ROTEIRO_DEMONSTRACAO.md`](docs/ROTEIRO_DEMONSTRACAO.md)
- Infra: [`template.yaml`](template.yaml)
- Pipeline: [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml)

## Módulos (uma Lambda cada)

| Pasta | Lambda | Responsável |
|---|---|---|
| `receber-avaliacao/` | `POST /avaliacao` → DynamoDB | Dev 1 |
| `notificar-critica/` | DynamoDB Stream → SNS (e-mail) | Dev 2 |
| `relatorio-semanal/` | EventBridge Scheduler → S3 | Dev 3 |

Transversal (Dev 5): IAM mínimo nas roles, logs/alarmes CloudWatch, testes de contrato, documentação e roteiro de demo.

## Contrato do item (DynamoDB)

Campos gravados por `receber-avaliacao` e lidos pelas outras Lambdas:

| Campo | Tipo | Exemplo |
|---|---|---|
| `id` | S | UUID |
| `descricao` | S | texto do feedback |
| `nota` | N | `0` a `10` |
| `dataEnvio` | S | ISO-8601 (`2026-11-15T12:00:00Z`) |

Avaliação crítica: `nota <= LimiteNotaCritica` (padrão **4**).

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

O CI também valida o `template.yaml` com `cfn-lint` e, após deploy na `main`, faz smoke test (`201` e `400` no `POST /avaliacao`).

## Remover tudo

```bash
sam delete --stack-name feedback-aulas-dev
```
