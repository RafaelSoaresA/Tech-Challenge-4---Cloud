package br.com.techchallenge.avaliacao.domain.avaliacao.exception;

public class NotaAvaliacaoInvalidaException extends RuntimeException {
    public NotaAvaliacaoInvalidaException() {
        super("A nota deve estar entre 0 e 10.");
    }
}
