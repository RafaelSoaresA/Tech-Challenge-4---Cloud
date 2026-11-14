package br.com.techchallenge.avaliacao.domain.avaliacao.exception;

public class NotaAvaliacaoObrigatoriaException extends RuntimeException {
    public NotaAvaliacaoObrigatoriaException() {
        super("A nota da avaliação é obrigatória.");
    }
}
