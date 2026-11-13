
package br.com.techchallenge.avaliacao.infra.avaliacao.persistence.repository;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import br.com.techchallenge.avaliacao.infra.avaliacao.mapper.AvaliacaoPersistenceMapper;
import br.com.techchallenge.avaliacao.infra.avaliacao.persistence.entitty.AvaliacaoJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DynamoDBAvaliacaoGateway implements AvaliacaoGateway {

    private final DynamoDbTable<AvaliacaoJpaEntity> tabela;
    private final AvaliacaoPersistenceMapper mapper;

    private static final Logger log =
            LoggerFactory.getLogger(DynamoDBAvaliacaoGateway.class);

    public DynamoDBAvaliacaoGateway(
            DynamoDbEnhancedClient client,
            AvaliacaoPersistenceMapper mapper) {

        this.tabela = client.table(
                "avaliacoes-dev",
                TableSchema.fromBean(AvaliacaoJpaEntity.class)
        );

        this.mapper = mapper;
    }

    @Override
    public Avaliacao save(Avaliacao avaliacao) {

        try {
            log.info("Iniciando persistência da avaliação: {}", avaliacao.getId());

            AvaliacaoJpaEntity entity = mapper.toJpa(avaliacao);

            log.info("Entidade convertida: id={}, descricao={}, nota={}",
                    entity.getId(),
                    entity.getDescricao(),
                    entity.getNota());

            tabela.putItem(entity);

            log.info("Avaliação salva com sucesso no DynamoDB");

            return mapper.toDomain(entity);

        } catch (Exception e) {
            log.error("Erro ao salvar avaliação no DynamoDB", e);
            throw e;
        }
    }

    @Override
    public Optional<Avaliacao> findById(UUID id) {
        AvaliacaoJpaEntity entity = tabela.getItem(
                Key.builder()
                        .partitionValue(id.toString())
                        .build()
        );
        return Optional.ofNullable(entity)
                .map(mapper::toDomain);    }

    @Override
    public List<Avaliacao> findAll() {

        return tabela.scan()
                .items()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {

        AvaliacaoJpaEntity entity = tabela.getItem(
                Key.builder()
                        .partitionValue(id.toString())
                        .build()
        );

        return entity != null;
    }

    @Override
    public void deleteById(UUID id) {

        tabela.deleteItem(
                Key.builder()
                        .partitionValue(id.toString())
                        .build()
        );
    }
}
