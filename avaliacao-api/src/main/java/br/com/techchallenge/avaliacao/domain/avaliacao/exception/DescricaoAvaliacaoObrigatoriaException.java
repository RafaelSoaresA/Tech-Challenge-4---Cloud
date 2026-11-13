package br.com.techchallenge.avaliacao.domain.avaliacao.exception;

public class DescricaoAvaliacaoObrigatoriaException extends RuntimeException{

    public DescricaoAvaliacaoObrigatoriaException() {
        super("A descrição da avaliação é obrigatória.");
    }
}
