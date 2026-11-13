package br.com.techchallenge.avaliacao.infra.avaliacao.mapper;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.infra.avaliacao.persistence.entitty.AvaliacaoJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AvaliacaoPersistenceMapper {

    public AvaliacaoJpaEntity toJpa(Avaliacao avaliacao) {
        return new AvaliacaoJpaEntity(
                avaliacao.getId().toString(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getData().toString()
        );
    }

    public Avaliacao toDomain(AvaliacaoJpaEntity avaliacaoJpaEntity) {
        return Avaliacao.builder()
                .id(UUID.fromString(avaliacaoJpaEntity.getId()))
                .descricao(avaliacaoJpaEntity.getDescricao())
                .nota(avaliacaoJpaEntity.getNota())
                .build();
    }

}
