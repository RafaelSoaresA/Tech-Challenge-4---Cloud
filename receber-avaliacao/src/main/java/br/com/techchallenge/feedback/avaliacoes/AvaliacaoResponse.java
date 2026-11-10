package br.com.techchallenge.feedback.avaliacoes;

/** Avaliação registrada, devolvida com HTTP 201. */
public record AvaliacaoResponse(String id, String descricao, double nota, String dataEnvio) {
}
