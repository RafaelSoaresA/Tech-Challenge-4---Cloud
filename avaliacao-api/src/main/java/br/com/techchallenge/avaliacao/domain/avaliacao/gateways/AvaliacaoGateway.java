package br.com.techchallenge.avaliacao.domain.avaliacao.gateways;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoGateway {
    Avaliacao save(Avaliacao avaliacao);
    Optional<Avaliacao> findById(UUID id);
    List<Avaliacao> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
