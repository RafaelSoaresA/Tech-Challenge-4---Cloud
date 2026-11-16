# Roteiro de demonstração (vídeo)

Use este roteiro na gravação. Ordem pensada para caber em poucos minutos e cobrir o que a FIAP pede.

## Antes de gravar

1. Confirme a região **us-east-1**.
2. Faça um POST “quente” na API (evita cold start feio na primeira cena).
3. Confirme a assinatura do e-mail SNS (inbox do admin).
4. Tenha o Postman (ou curl) e o console AWS abertos.

## Cenas sugeridas

1. **Repositório** — mostre o GitHub público, `template.yaml`, módulos das três Lambdas e o workflow em Actions verde.
2. **Arquitetura** — abra [`SEGURANCA_E_MONITORAMENTO.md`](SEGURANCA_E_MONITORAMENTO.md) ou desenhe o fluxo: API → DynamoDB → Stream → SNS; Scheduler → S3.
3. **Stack** — CloudFormation → stack `feedback-aulas-dev` → Resources (API, Lambdas, tabela, SNS, S3, alarmes).
4. **Segurança (US13)** — Lambda → Configuration → Permissions → role; mostre policy só da tabela/tópico/bucket. Mencione deploy por OIDC (sem access key no GitHub).
5. **Fluxo feliz** — `POST /avaliacao` com nota 9 → HTTP 201 → item no DynamoDB.
6. **Alerta crítico** — `POST /avaliacao` com nota 2 → e-mail com descrição, urgência e data; log `Alerta enviado` no CloudWatch.
7. **Monitoramento (US14)** — Log groups das três funções + alarmes `receber-avaliacao-dev-errors`, `notificar-critica-dev-errors`, `relatorio-semanal-dev-errors` + dashboard `feedback-aulas-dev-observabilidade`.
8. **Relatório** — S3 bucket de relatórios (objeto recente) ou invoke manual da Lambda de relatório.
9. **Qualidade (US15)** — Actions com `mvn verify` + smoke test; diga que há testes de contrato do item/evento.

## Comandos úteis (opcional na demo)

```bash
# URL da API
aws cloudformation describe-stacks --stack-name feedback-aulas-dev \
  --query "Stacks[0].Outputs[?OutputKey=='ApiUrl'].OutputValue" --output text

# Avaliação normal
curl -s -X POST "$API_URL/avaliacao" -H "Content-Type: application/json" \
  -d '{"descricao":"demo ok","nota":9}'

# Avaliação crítica
curl -s -X POST "$API_URL/avaliacao" -H "Content-Type: application/json" \
  -d '{"descricao":"demo critica","nota":2}'
```

## Encerramento (30 s)

Repetir: serverless na AWS, mínimo de duas Lambdas com responsabilidade única, segurança por IAM/OIDC, monitoramento com logs e alarmes, código e infra versionados no GitHub.
