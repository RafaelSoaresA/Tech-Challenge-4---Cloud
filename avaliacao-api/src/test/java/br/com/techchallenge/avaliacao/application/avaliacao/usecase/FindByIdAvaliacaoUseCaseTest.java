
package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.AvaliacaoNotFoundException;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do FindByIdAvaliacaoUseCase")
class FindByIdAvaliacaoUseCaseTest {

    @Mock
    private AvaliacaoGateway avaliacaoGateway;

    @InjectMocks
    private FindByIdAvaliacaoUseCase findByIdAvaliacaoUseCase;

    @Test
    @DisplayName("Deve retornar avaliação quando ela existir")
    void deveRetornarAvaliacaoQuandoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        Avaliacao avaliacao = Avaliacao.builder()
                .id(id)
                .descricao("Excelente atendimento")
                .nota(9)
                .build();

        when(avaliacaoGateway.findById(id))
                .thenReturn(Optional.of(avaliacao));

        // Act
        AvaliacaoOutput output = findByIdAvaliacaoUseCase.execute(id);

        // Assert
        assertNotNull(output);
        assertEquals(avaliacao.getId(), output.getId());
        assertEquals(avaliacao.getDescricao(), output.getDescricao());
        assertEquals(avaliacao.getNota(), output.getNota());
        assertEquals(avaliacao.getData(), output.getData());

        verify(avaliacaoGateway, times(1)).findById(id);

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando avaliação não existir")
    void deveLancarExcecaoQuandoAvaliacaoNaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(avaliacaoGateway.findById(id))
                .thenReturn(Optional.empty());

        // Act
        assertThrows(
                AvaliacaoNotFoundException.class,
                () -> findByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        verify(avaliacaoGateway, times(1)).findById(id);

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha na consulta")
    void devePropagarExcecaoQuandoFalharConsulta() {

        // Arrange
        UUID id = UUID.randomUUID();

        RuntimeException exception =
                new RuntimeException("Erro ao consultar avaliação");

        when(avaliacaoGateway.findById(id))
                .thenThrow(exception);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> findByIdAvaliacaoUseCase.execute(id)
        );

        // Assert
        assertSame(exception, thrown);

        verify(avaliacaoGateway, times(1)).findById(id);

        verifyNoMoreInteractions(avaliacaoGateway);
    }
}
