package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.domain.avaliacao.exception.AvaliacaoNotFoundException;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;

import java.util.UUID;

public class DeleteByIdAvaliacaoUseCase {

    private final AvaliacaoGateway avaliacaoGateway;

    public DeleteByIdAvaliacaoUseCase(AvaliacaoGateway avaliacaoGateway) {
        this.avaliacaoGateway = avaliacaoGateway;
    }

    public void execute(UUID id) {
        if (!avaliacaoGateway.existsById(id)) {
            throw new AvaliacaoNotFoundException();
        }

        avaliacaoGateway.deleteById(id);
    }
}
