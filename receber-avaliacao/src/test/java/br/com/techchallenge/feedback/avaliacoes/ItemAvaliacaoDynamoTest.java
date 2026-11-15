package br.com.techchallenge.feedback.avaliacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Trava o contrato do item no DynamoDB (US15): se alguém renomear campo,
 * notificar-critica e relatorio-semanal quebram.
 */
class ItemAvaliacaoDynamoTest {

    @Test
    void itemContemCamposEsperadosPelosConsumidores() {
        Map<String, AttributeValue> item = ItemAvaliacaoDynamo.de(
                "id-1", "aula ruim", 2.0, "2026-11-15T12:00:00Z");

        assertEquals(Set.of("id", "descricao", "nota", "dataEnvio"), item.keySet());
        assertEquals("id-1", item.get(ItemAvaliacaoDynamo.ID).s());
        assertEquals("aula ruim", item.get(ItemAvaliacaoDynamo.DESCRICAO).s());
        assertEquals("2.0", item.get(ItemAvaliacaoDynamo.NOTA).n());
        assertEquals("2026-11-15T12:00:00Z", item.get(ItemAvaliacaoDynamo.DATA_ENVIO).s());
        assertTrue(item.get(ItemAvaliacaoDynamo.NOTA).type() == AttributeValue.Type.N);
    }
}
