package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.dto.CreateAvaliacaoInput;
import br.com.techchallenge.avaliacao.application.avaliacao.mapper.AvaliacaoApplicationMapper;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoEventPublisher;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;

public class CreateAvaliacaoUseCase {

    private final AvaliacaoGateway avaliacaoGateway;
    private final AvaliacaoEventPublisher eventPublisher;

    public CreateAvaliacaoUseCase(AvaliacaoGateway avaliacaoGateway,
                                  AvaliacaoEventPublisher eventPublisher) {
        this.avaliacaoGateway = avaliacaoGateway;
        this.eventPublisher = eventPublisher;
    }

    public AvaliacaoOutput execute(CreateAvaliacaoInput input) {
        Avaliacao avaliacao = AvaliacaoApplicationMapper.toDomain(input);
        var savedAvaliacao = avaliacaoGateway.save(avaliacao);

        eventPublisher.publishAvaliacaoCriada(savedAvaliacao);

        return AvaliacaoApplicationMapper.toOuput(savedAvaliacao);
    }
}
