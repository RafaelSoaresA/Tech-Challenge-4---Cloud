package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.mapper.AvaliacaoApplicationMapper;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;

import java.util.List;

public class ListAvaliacaoUseCase {

    private final AvaliacaoGateway avaliacaoGateway;

    public ListAvaliacaoUseCase(AvaliacaoGateway avaliacaoGateway) {
        this.avaliacaoGateway = avaliacaoGateway;
    }

    public List<AvaliacaoOutput> execute(){
        return avaliacaoGateway.findAll().stream().map(AvaliacaoApplicationMapper::toOuput).toList();
    }
}
