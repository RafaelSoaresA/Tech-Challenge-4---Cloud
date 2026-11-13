package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.mapper.AvaliacaoApplicationMapper;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.AvaliacaoNotFoundException;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;

import java.util.UUID;

public class FindByIdAvaliacaoUseCase {

    private final AvaliacaoGateway gateway;

    public FindByIdAvaliacaoUseCase(AvaliacaoGateway gateway) {
        this.gateway = gateway;
    }

    public AvaliacaoOutput execute(UUID id) {
        Avaliacao avaliacao = gateway.findById(id)
                .orElseThrow(AvaliacaoNotFoundException::new);

        return AvaliacaoApplicationMapper.toOuput(avaliacao);
    }
}
