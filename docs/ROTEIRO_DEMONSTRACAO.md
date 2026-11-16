# Roteiro de demonstração (vídeo)

Use este roteiro na gravação. Ordem pensada para caber em poucos minutos e cobrir o que a FIAP pede.

## Antes de gravar

1. Confirme a região **us-east-1**.
2. Faça um POST na API (a primeira chamada do Beanstalk pode demorar).
3. Confirme a assinatura do e-mail SNS (inbox do admin).
4. Tenha o Postman (ou curl) e o console AWS abertos.

## Cenas sugeridas

1. **Repositório** — GitHub público, `template.yaml`, `avaliacao-api` e as duas Lambdas, workflow em Actions verde.
2. **Arquitetura** — abra [`SEGURANCA_E_MONITORAMENTO.md`](SEGURANCA_E_MONITORAMENTO.md): API Gateway → Beanstalk → DynamoDB → Stream → SNS; Scheduler → S3.
3. **Stack** — CloudFormation → stack `feedback-aulas-dev` → ambiente `feedback-api-dev`, Lambdas, tabela, tópicos SNS, bucket.
4. **Segurança (US13)** — role da instância (DynamoDB + SNS de eventos) e role de cada Lambda. Deploy por OIDC, sem access key no GitHub.
5. **Fluxo feliz** — `POST /api/avaliacao/v1` com nota 9 → HTTP 201 → item no DynamoDB. Essa nota não gera e-mail.
6. **Alerta** — `POST /api/avaliacao/v1` com nota 2 → e-mail com descrição, urgência e data; log `Alerta enviado` no CloudWatch da Lambda `notificar-critica-dev`.
7. **Monitoramento (US14)** — logs do Beanstalk e das duas Lambdas, alarmes `notificar-critica-dev-errors` e `relatorio-semanal-dev-errors`, dashboard `feedback-aulas-dev-observabilidade`.
8. **Relatório** — objeto recente no bucket de relatórios, ou invoke manual da Lambda de relatório.
9. **Qualidade (US15)** — Actions com `mvn verify` da API e das Lambdas, mais o smoke test `201` / `400`.

## Comandos úteis (opcional na demo)

```bash
# URL da API (já inclui o host; a rota fica em /api/avaliacao/v1)
aws cloudformation describe-stacks --stack-name feedback-aulas-dev \
  --query "Stacks[0].Outputs[?OutputKey=='ApiUrl'].OutputValue" --output text

# Avaliação normal (não envia e-mail)
curl -s -X POST "$API_URL/api/avaliacao/v1" -H "Content-Type: application/json" \
  -d '{"descricao":"demo ok","nota":9}'

# Avaliação crítica
curl -s -X POST "$API_URL/api/avaliacao/v1" -H "Content-Type: application/json" \
  -d '{"descricao":"demo critica","nota":2}'
```

## Encerramento (30 s)

API no Elastic Beanstalk, duas Lambdas com responsabilidade única, segurança por IAM/OIDC, monitoramento com logs e alarmes, código e infra versionados no GitHub.
