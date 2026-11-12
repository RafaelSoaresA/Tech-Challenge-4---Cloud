package br.com.techchallenge.feedback.notificacoes;

import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoEvento;
import br.com.techchallenge.feedback.notificacoes.dynamodb.DynamoDbObjectMapperCustomizer;
import br.com.techchallenge.feedback.notificacoes.sns.PublicadorAlerta;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificarCriticaHandlerTest {

    private static final ObjectMapper MAPPER = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        new DynamoDbObjectMapperCustomizer().customize(mapper);
        return mapper;
    }

    private final List<AvaliacaoEvento> publicados = new ArrayList<>();
    private final PublicadorAlerta publicador = (avaliacao, urgencia, enviadoEm) -> publicados.add(avaliacao);
    private final NotificarCriticaHandler handler = new NotificarCriticaHandler(publicador, 4);

    @Test
    void publicaApenasAvaliacoesCriticas() throws Exception {
        DynamodbEvent evento = evento(registro("critica", "2"), registro("normal", "9"));

        Map<String, Integer> resultado = handler.handleRequest(evento, null);

        assertEquals(List.of("critica"), publicados.stream().map(AvaliacaoEvento::id).toList());
        assertEquals(Map.of("processados", 2, "alertasEnviados", 1, "ignorados", 0), resultado);
    }

    @Test
    void ignoraEventoQueNaoEhInsert() throws Exception {
        DynamodbEvent evento = MAPPER.readValue(
                "{\"Records\":[{\"eventID\":\"evt-1\",\"eventName\":\"MODIFY\",\"dynamodb\":{\"NewImage\":"
                        + imagem("a1", "1") + "}}]}",
                DynamodbEvent.class);

        Map<String, Integer> resultado = handler.handleRequest(evento, null);

        assertTrue(publicados.isEmpty());
        assertEquals(Map.of("processados", 0, "alertasEnviados", 0, "ignorados", 1), resultado);
    }

    @Test
    void ignoraRegistroSemNota() throws Exception {
        DynamodbEvent evento = MAPPER.readValue(
                "{\"Records\":[{\"eventID\":\"evt-1\",\"eventName\":\"INSERT\",\"dynamodb\":{\"NewImage\":"
                        + "{\"id\":{\"S\":\"a1\"}}}}]}",
                DynamodbEvent.class);

        Map<String, Integer> resultado = handler.handleRequest(evento, null);

        assertTrue(publicados.isEmpty());
        assertEquals(Map.of("processados", 0, "alertasEnviados", 0, "ignorados", 1), resultado);
    }

    @Test
    void falhaDePublicacaoPropagaErroParaReprocessarLote() throws Exception {
        PublicadorAlerta comFalha = (avaliacao, urgencia, enviadoEm) -> {
            throw new IllegalStateException("SNS indisponivel");
        };
        NotificarCriticaHandler handlerComFalha = new NotificarCriticaHandler(comFalha, 4);

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> handlerComFalha.handleRequest(evento(registro("a1", "2")), null));

        assertTrue(erro.getMessage().contains("lote sera reprocessado"));
    }

    @Test
    void respeitaLimiteConfigurado() throws Exception {
        NotificarCriticaHandler handlerLimiteSeis = new NotificarCriticaHandler(publicador, 6);

        handlerLimiteSeis.handleRequest(evento(registro("a1", "6")), null);

        assertEquals(1, publicados.size());
    }

    private static DynamodbEvent evento(String... registros) throws Exception {
        return MAPPER.readValue("{\"Records\":[" + String.join(",", registros) + "]}", DynamodbEvent.class);
    }

    private static String registro(String id, String nota) {
        return "{\"eventID\":\"evt-" + id + "\",\"eventName\":\"INSERT\",\"dynamodb\":{\"NewImage\":"
                + imagem(id, nota) + "}}";
    }

    private static String imagem(String id, String nota) {
        return "{\"id\":{\"S\":\"" + id + "\"},\"descricao\":{\"S\":\"teste\"},\"nota\":{\"N\":\"" + nota
                + "\"},\"dataEnvio\":{\"S\":\"2026-11-09T12:00:00Z\"}}";
    }
}
