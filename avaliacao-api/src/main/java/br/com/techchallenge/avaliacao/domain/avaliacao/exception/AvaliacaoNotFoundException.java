package br.com.techchallenge.avaliacao.domain.avaliacao.exception;

public class AvaliacaoNotFoundException extends RuntimeException {
    public AvaliacaoNotFoundException(){
        super("Avaliação não encontrada.");
    }
}
