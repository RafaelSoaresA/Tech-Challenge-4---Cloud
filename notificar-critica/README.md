# notificar-critica

Lambda Quarkus (Java 21) da US04/US05/US06: consome o DynamoDB Stream de avaliações, classifica a urgência e publica alerta no SNS.

Fluxo: `DynamoDB Stream (INSERT)` → `notificar-critica` → `SNS` → e-mail do administrador (`fiap.tech.challenge.4.2026@gmail.com`).

## Regras

| Requisito | Implementação |
| --- | --- |
| US04 — classificar urgência | `nota <= LIMITE_NOTA_CRITICA` (padrão `4`) ⇒ `CRITICA`; acima ⇒ `NORMAL` |
| US05 — Lambda independente | `NotificarCriticaHandler`, disparado pelo stream, com retry de lote em caso de falha |
| US06 — alerta ao administrador | Publicação no tópico SNS com descrição, nota, urgência e data de envio |

## Estrutura

```text
notificar-critica/
├── pom.xml
├── events/stream_critical.json
└── src/main/java/br/com/techchallenge/feedback/notificacoes/
    ├── NotificarCriticaHandler.java
    ├── domain/   (AvaliacaoEvento, AvaliacaoMapper, ClassificadorUrgencia, Urgencia)
    ├── dynamodb/ (mixins do evento + customizer do ObjectMapper)
    └── sns/      (PublicadorAlerta, SnsPublicadorAlerta)
```

## Contrato

Campos lidos do `NewImage`:

| Campo | Tipo | Uso |
| --- | --- | --- |
| `id` | `S` | identificação da avaliação |
| `descricao` | `S` | corpo do e-mail |
| `nota` | `N` | classificação de urgência |
| `dataEnvio` ou `data` | `S` | data da avaliação (aceita os dois nomes) |

Variáveis de ambiente:

| Variável | Obrigatória | Padrão |
| --- | --- | --- |
| `TOPICO_ALERTAS_ARN` | sim | — |
| `LIMITE_NOTA_CRITICA` | não | `4` |
| `QUARKUS_LAMBDA_HANDLER` | não | `notificar-critica` |

Destinatário: definido no `template.yaml` pelo parâmetro `AdminEmail`, assinado no tópico SNS (protocolo `email`). Padrão `fiap.tech.challenge.4.2026@gmail.com` — o endereço precisa **confirmar a assinatura** que a AWS envia por e-mail.

Mensagem publicada:

```text
Avaliação CRÍTICA recebida

Descrição: <descricao>
Nota: <nota>
Urgência: CRITICA
Data da avaliação: <data>
Data de envio do alerta: <ISO 8601 UTC>
ID da avaliação: <id>
```

## Build, testes e execução local

```bash
# na raiz do repositório
mvn -pl notificar-critica -am test
mvn -pl notificar-critica -am package   # gera notificar-critica/target/function.zip

# invocação local do evento de exemplo
sam local invoke NotificarCriticaFunction \
  --event notificar-critica/events/stream_critical.json \
  --env-vars env.json
```

`env.json`:

```json
{
  "NotificarCriticaFunction": {
    "TOPICO_ALERTAS_ARN": "arn:aws:sns:us-east-1:123456789012:alertas-criticos-dev",
    "LIMITE_NOTA_CRITICA": "4"
  }
}
```

A infraestrutura (`template.yaml`) já aponta `CodeUri` para `notificar-critica/target/function.zip` e define `QUARKUS_LAMBDA_HANDLER=notificar-critica`.
