package br.com.techchallenge.feedback.avaliacoes;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Contrato do item persistido (US02/US03): nomes e tipos que as Lambdas de
 * notificação e relatório leem. Mudança aqui exige alinhar Dev 2 e Dev 3.
 */
public final class ItemAvaliacaoDynamo {

    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";
    public static final String NOTA = "nota";
    public static final String DATA_ENVIO = "dataEnvio";

    private ItemAvaliacaoDynamo() {
    }

    public static Map<String, AttributeValue> de(String id, String descricao, double nota, String dataEnvio) {
        return Map.of(
                ID, AttributeValue.fromS(id),
                DESCRICAO, AttributeValue.fromS(descricao),
                NOTA, AttributeValue.fromN(Double.toString(nota)),
                DATA_ENVIO, AttributeValue.fromS(dataEnvio));
    }
}
