package br.com.techchallenge.feedback.avaliacoes;

/** Corpo do POST /avaliacao. */
public record AvaliacaoRequest(String descricao, Double nota) {
}
