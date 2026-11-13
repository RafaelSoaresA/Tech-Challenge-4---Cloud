
package br.com.techchallenge.avaliacao.infra.avaliacao.persistence.repository;

import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.infra.avaliacao.mapper.AvaliacaoPersistenceMapper;
import br.com.techchallenge.avaliacao.infra.avaliacao.persistence.entitty.AvaliacaoJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DynamoDBAvaliacaoGateway")
class DynamoDBAvaliacaoGatewayTest {

    @Mock
    private DynamoDbEnhancedClient client;

    @Mock
    private AvaliacaoPersistenceMapper mapper;

    @Mock
    private DynamoDbTable<AvaliacaoJpaEntity> tabela;

    @Mock
    private AvaliacaoJpaEntity entity;

    @Mock
    private PageIterable<AvaliacaoJpaEntity> pageIterable;

    @Mock
    private SdkIterable<AvaliacaoJpaEntity> items;

    @InjectMocks
    private DynamoDBAvaliacaoGateway gateway;

    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        when(client.table(
                eq("avaliacao_jpa_entity"),
                any(TableSchema.class)
        )).thenReturn(tabela);

        gateway = new DynamoDBAvaliacaoGateway(client, mapper);

        avaliacao = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Excelente atendimento")
                .nota(10)
                .build();
    }

    @Test
    @DisplayName("Deve salvar avaliação com sucesso")
    void deveSalvarAvaliacaoComSucesso() {

        // Arrange
        when(mapper.toJpa(avaliacao))
                .thenReturn(entity);

        when(mapper.toDomain(entity))
                .thenReturn(avaliacao);

        // Act
        Avaliacao resultado = gateway.save(avaliacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(avaliacao.getId(), resultado.getId());
        assertEquals(avaliacao.getDescricao(), resultado.getDescricao());
        assertEquals(avaliacao.getNota(), resultado.getNota());

        verify(mapper, times(1)).toJpa(avaliacao);
        verify(tabela, times(1)).putItem(entity);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha ao salvar")
    void devePropagarExcecaoQuandoFalharSalvamento() {

        // Arrange
        RuntimeException exception =
                new RuntimeException("Erro ao salvar no DynamoDB");

        when(mapper.toJpa(avaliacao))
                .thenReturn(entity);

        doThrow(exception)
                .when(tabela)
                .putItem(entity);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> gateway.save(avaliacao)
        );

        // Assert
        assertSame(exception, thrown);

        verify(mapper).toJpa(avaliacao);
        verify(tabela).putItem(entity);
        verify(mapper, never()).toDomain(any(AvaliacaoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve encontrar avaliação pelo ID")
    void deveEncontrarAvaliacaoPorId() {

        // Arrange
        UUID id = avaliacao.getId();

        when(tabela.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(entity);

        when(mapper.toDomain(entity))
                .thenReturn(avaliacao);

        // Act
        Optional<Avaliacao> resultado = gateway.findById(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(avaliacao.getDescricao(), resultado.get().getDescricao());
        assertEquals(avaliacao.getNota(), resultado.get().getNota());

        verify(tabela, times(1))
                .getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class));

        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando avaliação não existir")
    void deveRetornarOptionalVazioQuandoAvaliacaoNaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(tabela.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(null);

        // Act
        Optional<Avaliacao> resultado = gateway.findById(id);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(tabela, times(1))
                .getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class));

        verify(mapper, never()).toDomain(any(AvaliacaoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve retornar todas as avaliações")
    void deveRetornarTodasAvaliacoes() {

        // Arrange
        Avaliacao segundaAvaliacao = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Atendimento satisfatório")
                .nota(8)
                .build();

        AvaliacaoJpaEntity segundaEntity = mock(AvaliacaoJpaEntity.class);

        when(tabela.scan()).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(items);

        when(items.stream())
                .thenReturn(Stream.of(entity, segundaEntity));

        when(mapper.toDomain(entity))
                .thenReturn(avaliacao);

        when(mapper.toDomain(segundaEntity))
                .thenReturn(segundaAvaliacao);

        // Act
        List<Avaliacao> resultado = gateway.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(avaliacao.getId(), resultado.get(0).getId());
        assertEquals(avaliacao.getDescricao(), resultado.get(0).getDescricao());

        assertEquals(segundaAvaliacao.getId(), resultado.get(1).getId());
        assertEquals(segundaAvaliacao.getDescricao(), resultado.get(1).getDescricao());

        verify(tabela, times(1)).scan();
        verify(mapper, times(1)).toDomain(entity);
        verify(mapper, times(1)).toDomain(segundaEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existirem avaliações")
    void deveRetornarListaVaziaQuandoNaoExistiremAvaliacoes() {

        // Arrange
        when(tabela.scan()).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(items);

        when(items.stream()).thenReturn(Stream.empty());

        // Act
        List<Avaliacao> resultado = gateway.findAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(tabela, times(1)).scan();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando avaliação existir")
    void deveRetornarVerdadeiroQuandoAvaliacaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(tabela.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(entity);

        // Act
        boolean resultado = gateway.existsById(id);

        // Assert
        assertTrue(resultado);

        verify(tabela, times(1))
                .getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class));
    }

    @Test
    @DisplayName("Deve retornar falso quando avaliação não existir")
    void deveRetornarFalsoQuandoAvaliacaoNaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(tabela.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(null);

        // Act
        boolean resultado = gateway.existsById(id);

        // Assert
        assertFalse(resultado);

        verify(tabela, times(1))
                .getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class));
    }

    @Test
    @DisplayName("Deve excluir avaliação pelo ID")
    void deveExcluirAvaliacaoPorId() {

        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        assertDoesNotThrow(() -> gateway.deleteById(id));

        // Assert
        verify(tabela, times(1))
                .deleteItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class));
    }
}
