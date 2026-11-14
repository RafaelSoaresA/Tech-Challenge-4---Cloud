
package br.com.techchallenge.avaliacao.infra.avaliacao.messaging;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.Instant;
import java.util.Map;

public class SnsAvaliacaoEventPublisher implements AvaliacaoEventPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    public SnsAvaliacaoEventPublisher(
            SnsClient snsClient,
            ObjectMapper objectMapper,
            String topicArn
    ) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }

    @Override
    public void publishAvaliacaoCriada(Avaliacao avaliacao) {
        try {
            AvaliacaoCriadaEvent event = new AvaliacaoCriadaEvent(
                    "AVALIACAO_CRIADA",
                    Instant.now(),
                    new DadosAvaliacao(
                            avaliacao.getId(),
                            avaliacao.getDescricao(),
                            avaliacao.getNota()
                    )
            );

            String payload = objectMapper.writeValueAsString(event);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(payload)
                    .subject("AvaliacaoCriada")
                    .messageAttributes(Map.of(
                            "eventType",
                            MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue("AVALIACAO_CRIADA")
                                    .build()
                    ))
                    .build();

            PublishResponse response = snsClient.publish(request);

            System.out.println(
                    "Evento publicado no SNS. MessageId: " + response.messageId()
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Erro ao serializar evento de avaliação", e
            );
        }
    }

    public record AvaliacaoCriadaEvent(
            String eventType,
            Instant occurredAt,
            DadosAvaliacao data
    ) {}

    public record DadosAvaliacao(
            Object id,
            String descricao,
            Integer nota
    ) {}
}
