package br.com.techchallenge.feedback.notificacoes.domain;

import br.com.techchallenge.feedback.notificacoes.dynamodb.DynamoDbObjectMapperCustomizer;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliacaoMapperTest {

    private static final ObjectMapper MAPPER = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        new DynamoDbObjectMapperCustomizer().customize(mapper);
        return mapper;
    }

    @Test
    void extraiCamposDoNewImage() throws Exception {
        DynamodbEvent evento = eventoCom("""
                {
                  "id": {"S": "a1"},
                  "descricao": {"S": "Aula confusa"},
                  "nota": {"N": "2"},
                  "dataEnvio": {"S": "2026-11-09T12:00:00Z"}
                }
                """);

        Optional<AvaliacaoEvento> resultado = AvaliacaoMapper.extrair(evento.getRecords().get(0));

        assertTrue(resultado.isPresent());
        assertEquals("a1", resultado.get().id());
        assertEquals("Aula confusa", resultado.get().descricao());
        assertEquals(2.0, resultado.get().nota());
        assertEquals("2026-11-09T12:00:00Z", resultado.get().dataAvaliacao());
    }

    @Test
    void aceitaCampoDataDaApiJava() throws Exception {
        DynamodbEvent evento = eventoCom("""
                {
                  "id": {"S": "a1"},
                  "descricao": {"S": "Aula confusa"},
                  "nota": {"N": "2"},
                  "data": {"S": "2026-11-09T12:00:00Z"}
                }
                """);

        Optional<AvaliacaoEvento> resultado = AvaliacaoMapper.extrair(evento.getRecords().get(0));

        assertTrue(resultado.isPresent());
        assertEquals("2026-11-09T12:00:00Z", resultado.get().dataAvaliacao());
    }

    @Test
    void ignoraRegistroSemNota() throws Exception {
        DynamodbEvent evento = eventoCom("""
                {
                  "id": {"S": "a1"},
                  "descricao": {"S": "Aula confusa"}
                }
                """);

        assertTrue(AvaliacaoMapper.extrair(evento.getRecords().get(0)).isEmpty());
    }

    @Test
    void ignoraNotaNaoNumerica() throws Exception {
        DynamodbEvent evento = eventoCom("""
                {
                  "id": {"S": "a1"},
                  "descricao": {"S": "Aula confusa"},
                  "nota": {"N": "duas"}
                }
                """);

        assertTrue(AvaliacaoMapper.extrair(evento.getRecords().get(0)).isEmpty());
    }

    private static DynamodbEvent eventoCom(String newImage) throws Exception {
        String json = "{\"Records\":[{\"eventID\":\"evt-1\",\"eventName\":\"INSERT\",\"dynamodb\":{\"NewImage\":"
                + newImage + "}}]}";
        return MAPPER.readValue(json, DynamodbEvent.class);
    }
}
