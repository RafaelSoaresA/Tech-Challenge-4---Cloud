package br.com.techchallenge.feedback.notificacoes.sns;

import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoEvento;
import br.com.techchallenge.feedback.notificacoes.domain.Urgencia;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Publica no tópico SNS da infraestrutura (quarkus-amazon-sns fornece o SnsClient).
 * A assinatura de e-mail do tópico entrega no endereço configurado (AdminEmail no template).
 */
@ApplicationScoped
public class SnsPublicadorAlerta implements PublicadorAlerta {

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(FUSO_BRASILIA);

    private final SnsClient snsClient;
    private final String topicoArn;
    private final String assunto;

    @Inject
    public SnsPublicadorAlerta(
            SnsClient snsClient,
            @ConfigProperty(name = "topico.alertas.arn", defaultValue = "") String topicoArn,
            @ConfigProperty(name = "alerta.assunto", defaultValue = "[Feedback] Avaliacao critica") String assunto) {
        this.snsClient = snsClient;
        this.topicoArn = topicoArn;
        this.assunto = assunto;
    }

    @Override
    public void publicar(AvaliacaoEvento avaliacao, Urgencia urgencia, Instant enviadoEm) {
        if (topicoArn == null || topicoArn.isBlank()) {
            throw new IllegalStateException("TOPICO_ALERTAS_ARN nao configurado");
        }

        PublishRequest requisicao = PublishRequest.builder()
                .topicArn(topicoArn)
                .subject(assunto)
                .message(montarMensagem(avaliacao, urgencia, enviadoEm))
                .messageAttributes(Map.of(
                        "urgencia", atributo(urgencia.name()),
                        "avaliacaoId", atributo(avaliacao.id() == null ? "desconhecido" : avaliacao.id())))
                .build();

        snsClient.publish(requisicao);
    }

    static String montarMensagem(AvaliacaoEvento avaliacao, Urgencia urgencia, Instant enviadoEm) {
        return """
                Avaliação com urgência %s recebida

                Descrição: %s
                Nota: %s
                Urgência: %s
                Data da avaliação: %s
                Data de envio do alerta: %s
                ID da avaliação: %s
                """.formatted(
                urgencia.name(),
                valorOuPadrao(avaliacao.descricao()),
                formatarNota(avaliacao.nota()),
                urgencia.name(),
                formatarDataHora(avaliacao.dataAvaliacao()),
                formatarDataHora(enviadoEm),
                valorOuPadrao(avaliacao.id()));
    }

    private static MessageAttributeValue atributo(String valor) {
        return MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(valor)
                .build();
    }

    private static String valorOuPadrao(String valor) {
        return valor == null || valor.isBlank() ? "não informado" : valor;
    }

    private static String formatarNota(double nota) {
        return nota == Math.rint(nota) ? String.valueOf((long) nota) : String.valueOf(nota);
    }

    private static String formatarDataHora(Instant instante) {
        return instante == null ? "não informado" : FORMATO_DATA_HORA.format(instante);
    }

    /** Aceita ISO 8601 com ou sem offset; formato desconhecido volta como veio. */
    private static String formatarDataHora(String iso) {
        if (iso == null || iso.isBlank()) {
            return "não informado";
        }
        try {
            return FORMATO_DATA_HORA.format(OffsetDateTime.parse(iso));
        } catch (DateTimeParseException ignorada) {
            try {
                return FORMATO_DATA_HORA.format(Instant.parse(iso));
            } catch (DateTimeParseException tambemIgnorada) {
                return iso;
            }
        }
    }
}
