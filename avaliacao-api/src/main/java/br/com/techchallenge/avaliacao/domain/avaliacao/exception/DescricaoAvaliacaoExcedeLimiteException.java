package br.com.techchallenge.avaliacao.domain.avaliacao.exception;

public class DescricaoAvaliacaoExcedeLimiteException extends RuntimeException {
    public DescricaoAvaliacaoExcedeLimiteException() {
        super("A descrição deve ter no máximo 2000 caracteres.");
    }
}
