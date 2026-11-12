package br.com.techchallenge.feedback.notificacoes.sns;

import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoEvento;
import br.com.techchallenge.feedback.notificacoes.domain.Urgencia;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SnsPublicadorAlertaTest {

    @Test
    void publicaMensagemComCamposExigidos() {
        SnsClient sns = mock(SnsClient.class);
        SnsPublicadorAlerta publicador = new SnsPublicadorAlerta(
                sns,
                "arn:aws:sns:us-east-1:123456789012:alertas-dev",
                "[Feedback] Avaliacao critica");

        publicador.publicar(
                new AvaliacaoEvento("a1", "Aula confusa", 2, "2026-11-09T12:00:00Z"),
                Urgencia.CRITICA,
                Instant.parse("2026-11-09T12:05:00Z"));

        ArgumentCaptor<PublishRequest> captor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(sns).publish(captor.capture());
        PublishRequest requisicao = captor.getValue();

        assertEquals("arn:aws:sns:us-east-1:123456789012:alertas-dev", requisicao.topicArn());
        assertTrue(requisicao.message().contains("Aula confusa"));
        assertTrue(requisicao.message().contains("Urgência: CRITICA"));
        assertTrue(requisicao.message().contains("Data de envio do alerta: 2026-11-09T12:05:00Z"));
        assertEquals("CRITICA", requisicao.messageAttributes().get("urgencia").stringValue());
        assertEquals("a1", requisicao.messageAttributes().get("avaliacaoId").stringValue());
    }

    @Test
    void falhaQuandoTopicoNaoConfigurado() {
        SnsPublicadorAlerta publicador = new SnsPublicadorAlerta(mock(SnsClient.class), "", "assunto");

        assertThrows(IllegalStateException.class, () -> publicador.publicar(
                new AvaliacaoEvento("a1", "Aula confusa", 2, null),
                Urgencia.CRITICA,
                Instant.now()));
    }
}
