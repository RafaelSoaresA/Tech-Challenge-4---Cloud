package br.com.techchallenge.feedback.notificacoes.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.techchallenge.feedback.notificacoes.dynamodb.DynamoDbObjectMapperCustomizer;

/**
 * Valida o contrato Dev1 → Dev2 (US03/US15): NewImage com id, descricao, nota (N)
 * e dataEnvio (S ISO-8601), como gravado por receber-avaliacao.
 */
class ContratoEventoAvaliacaoTest {

    private static final ObjectMapper MAPPER = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        new DynamoDbObjectMapperCustomizer().customize(mapper);
        return mapper;
    }

    @Test
    void extraiCamposDoContratoPersistidoPeloReceberAvaliacao() throws Exception {
        String json = """
                {
                  "Records": [{
                    "eventID": "evt-contrato",
                    "eventName": "INSERT",
                    "dynamodb": {
                      "NewImage": {
                        "id": { "S": "av-42" },
                        "descricao": { "S": "conteudo confuso" },
                        "nota": { "N": "3.0" },
                        "dataEnvio": { "S": "2026-11-15T15:00:00Z" }
                      }
                    }
                  }]
                }
                """;

        DynamodbEvent evento = MAPPER.readValue(json, DynamodbEvent.class);
        AvaliacaoEvento avaliacao = AvaliacaoMapper.extrair(evento.getRecords().get(0)).orElseThrow();

        assertEquals("av-42", avaliacao.id());
        assertEquals("conteudo confuso", avaliacao.descricao());
        assertEquals(3.0, avaliacao.nota());
        assertEquals("2026-11-15T15:00:00Z", avaliacao.dataAvaliacao());
        assertEquals(Urgencia.HIGH, ClassificadorUrgencia.classificar(avaliacao.nota(), 4));
    }
}
