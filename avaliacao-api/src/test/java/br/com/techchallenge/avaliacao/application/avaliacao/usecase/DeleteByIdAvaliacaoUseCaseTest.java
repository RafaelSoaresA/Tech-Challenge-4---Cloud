
package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.domain.avaliacao.exception.AvaliacaoNotFoundException;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DeleteByIdAvaliacaoUseCase")
class DeleteByIdAvaliacaoUseCaseTest {

    @Mock
    private AvaliacaoGateway avaliacaoGateway;

    @InjectMocks
    private DeleteByIdAvaliacaoUseCase deleteByIdAvaliacaoUseCase;

    @Test
    @DisplayName("Deve excluir avaliação quando ela existir")
    void deveExcluirAvaliacaoQuandoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(avaliacaoGateway.existsById(id))
                .thenReturn(true);

        // Act
        assertDoesNotThrow(
                () -> deleteByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        verify(avaliacaoGateway, times(1)).existsById(id);
        verify(avaliacaoGateway, times(1)).deleteById(id);

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando avaliação não existir")
    void deveLancarExcecaoQuandoAvaliacaoNaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(avaliacaoGateway.existsById(id))
                .thenReturn(false);

        // Act
        assertThrows(
                AvaliacaoNotFoundException.class,
                () -> deleteByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        verify(avaliacaoGateway, times(1)).existsById(id);

        verify(avaliacaoGateway, never()).deleteById(any(UUID.class));

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha na verificação")
    void devePropagarExcecaoQuandoFalharVerificacao() {

        // Arrange
        UUID id = UUID.randomUUID();

        RuntimeException exception =
                new RuntimeException("Erro ao consultar avaliação");

        when(avaliacaoGateway.existsById(id))
                .thenThrow(exception);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deleteByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        assertSame(exception, thrown);

        verify(avaliacaoGateway, times(1)).existsById(id);
        verify(avaliacaoGateway, never()).deleteById(any(UUID.class));

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha na exclusão")
    void devePropagarExcecaoQuandoFalharExclusao() {

        // Arrange
        UUID id = UUID.randomUUID();

        RuntimeException exception =
                new RuntimeException("Erro ao excluir avaliação");

        when(avaliacaoGateway.existsById(id))
                .thenReturn(true);

        doThrow(exception)
                .when(avaliacaoGateway)
                .deleteById(id);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deleteByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        assertSame(exception, thrown);

        verify(avaliacaoGateway, times(1)).existsById(id);
        verify(avaliacaoGateway, times(1)).deleteById(id);

        verifyNoMoreInteractions(avaliacaoGateway);
    }
}
