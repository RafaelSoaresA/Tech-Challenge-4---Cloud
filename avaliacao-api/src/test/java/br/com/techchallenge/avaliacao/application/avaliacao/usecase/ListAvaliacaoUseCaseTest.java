
package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ListAvaliacaoUseCase")
class ListAvaliacaoUseCaseTest {

    @Mock
    private AvaliacaoGateway avaliacaoGateway;

    @InjectMocks
    private ListAvaliacaoUseCase listAvaliacaoUseCase;

    @Test
    @DisplayName("Deve retornar todas as avaliações com sucesso")
    void deveRetornarTodasAvaliacoesComSucesso() {

        // Arrange
        Avaliacao avaliacao1 = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Excelente atendimento")
                .nota(10)
                .build();

        Avaliacao avaliacao2 = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Atendimento satisfatório")
                .nota(8)
                .build();

        Avaliacao avaliacao3 = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Atendimento razoável")
                .nota(6)
                .build();

        when(avaliacaoGateway.findAll())
                .thenReturn(List.of(avaliacao1, avaliacao2, avaliacao3));

        // Act
        List<AvaliacaoOutput> output = listAvaliacaoUseCase.execute();

        // Assert
        assertNotNull(output);
        assertEquals(3, output.size());

        verify(avaliacaoGateway, times(1)).findAll();

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existirem avaliações")
    void deveRetornarListaVaziaQuandoNaoExistiremAvaliacoes() {

        // Arrange
        when(avaliacaoGateway.findAll())
                .thenReturn(List.of());

        // Act
        List<AvaliacaoOutput> output = listAvaliacaoUseCase.execute();

        // Assert
        assertNotNull(output);
        assertTrue(output.isEmpty());

        verify(avaliacaoGateway, times(1)).findAll();

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve converter corretamente as entidades em DTOs")
    void deveConverterCorretamenteEntidadesEmDtos() {

        // Arrange
        Avaliacao avaliacao1 = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Excelente atendimento")
                .nota(10)
                .build();

        Avaliacao avaliacao2 = Avaliacao.builder()
                .id(UUID.randomUUID())
                .descricao("Atendimento satisfatório")
                .nota(8)
                .build();

        when(avaliacaoGateway.findAll())
                .thenReturn(List.of(avaliacao1, avaliacao2));

        // Act
        List<AvaliacaoOutput> output = listAvaliacaoUseCase.execute();

        // Assert
        assertEquals(2, output.size());

        assertEquals(avaliacao1.getId(), output.get(0).getId());
        assertEquals(avaliacao1.getDescricao(), output.get(0).getDescricao());
        assertEquals(avaliacao1.getNota(), output.get(0).getNota());
        assertEquals(avaliacao1.getData(), output.get(0).getData());

        assertEquals(avaliacao2.getId(), output.get(1).getId());
        assertEquals(avaliacao2.getDescricao(), output.get(1).getDescricao());
        assertEquals(avaliacao2.getNota(), output.get(1).getNota());
        assertEquals(avaliacao2.getData(), output.get(1).getData());

        verify(avaliacaoGateway, times(1)).findAll();

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha na consulta")
    void devePropagarExcecaoQuandoFalharConsulta() {

        // Arrange
        RuntimeException exception =
                new RuntimeException("Erro ao consultar avaliações");

        when(avaliacaoGateway.findAll())
                .thenThrow(exception);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> listAvaliacaoUseCase.execute()
        );

        // Assert
        assertSame(exception, thrown);

        verify(avaliacaoGateway, times(1)).findAll();

        verifyNoMoreInteractions(avaliacaoGateway);
    }
}
