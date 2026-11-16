package br.com.techchallenge.avaliacao.infra.avaliacao.mapper;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.infra.avaliacao.persistence.entitty.AvaliacaoDynamoDbEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AvaliacaoPersistenceMapper {

    public AvaliacaoDynamoDbEntity toJpa(Avaliacao avaliacao) {
        return new AvaliacaoDynamoDbEntity(
                avaliacao.getId().toString(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getData().toString()
        );
    }

    public Avaliacao toDomain(AvaliacaoDynamoDbEntity avaliacaoDynamoDbEntity) {
        return Avaliacao.builder()
                .id(UUID.fromString(avaliacaoDynamoDbEntity.getId()))
                .descricao(avaliacaoDynamoDbEntity.getDescricao())
                .nota(avaliacaoDynamoDbEntity.getNota())
                .build();
    }

}
