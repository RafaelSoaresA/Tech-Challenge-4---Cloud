package br.com.techchallenge.avaliacao.domain.avaliacao.gateways;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;

public interface AvaliacaoEventPublisher {
    void publishAvaliacaoCriada(Avaliacao avaliacao);
}