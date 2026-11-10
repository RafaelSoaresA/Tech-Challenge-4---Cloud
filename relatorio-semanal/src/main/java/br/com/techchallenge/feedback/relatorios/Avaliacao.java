package br.com.techchallenge.feedback.relatorios;

/** Avaliação lida do DynamoDB. */
public record Avaliacao(String descricao, double nota, String dataEnvio) {
}
