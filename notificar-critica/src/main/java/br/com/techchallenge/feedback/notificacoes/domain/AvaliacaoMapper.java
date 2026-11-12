package br.com.techchallenge.feedback.notificacoes.domain;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

import java.util.Map;
import java.util.Optional;

public final class AvaliacaoMapper {

    private AvaliacaoMapper() {
    }

    public static Optional<AvaliacaoEvento> extrair(DynamodbEvent.DynamodbStreamRecord registro) {
        if (registro == null || registro.getDynamodb() == null) {
            return Optional.empty();
        }

        Map<String, AttributeValue> imagem = registro.getDynamodb().getNewImage();
        if (imagem == null || imagem.isEmpty()) {
            return Optional.empty();
        }

        String notaTexto = numero(imagem.get("nota"));
        if (notaTexto == null) {
            return Optional.empty();
        }

        double nota;
        try {
            nota = Double.parseDouble(notaTexto);
        } catch (NumberFormatException excecao) {
            return Optional.empty();
        }

        String dataAvaliacao = texto(imagem.get("dataEnvio"));
        if (dataAvaliacao == null) {
            dataAvaliacao = texto(imagem.get("data"));
        }

        return Optional.of(new AvaliacaoEvento(
                texto(imagem.get("id")),
                texto(imagem.get("descricao")),
                nota,
                dataAvaliacao));
    }

    private static String texto(AttributeValue valor) {
        return valor == null ? null : valor.getS();
    }

    private static String numero(AttributeValue valor) {
        return valor == null ? null : valor.getN();
    }
}
