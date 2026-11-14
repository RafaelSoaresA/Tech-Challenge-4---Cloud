
package br.com.techchallenge.avaliacao.application.avaliacao.usecase;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.dto.CreateAvaliacaoInput;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CreateAvaliacaoUseCase")
class CreateAvaliacaoUseCaseTest {

    @Mock
    private AvaliacaoGateway avaliacaoGateway;

    @InjectMocks
    private CreateAvaliacaoUseCase createAvaliacaoUseCase;

    private CreateAvaliacaoInput input;

    @BeforeEach
    void setUp() {
        input = CreateAvaliacaoInput.builder()
                .descricao("Excelente atendimento")
                .nota(9)
                .build();
    }

    @Test
    @DisplayName("Deve criar uma avaliação com sucesso")
    void deveCriarAvaliacaoComSucesso() {

        // Arrange
        when(avaliacaoGateway.save(any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvaliacaoOutput output = createAvaliacaoUseCase.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.getId());
        assertEquals(input.getDescricao(), output.getDescricao());
        assertEquals(input.getNota(), output.getNota());
        assertNotNull(output.getData());

        verify(avaliacaoGateway, times(1))
                .save(any(Avaliacao.class));

        verifyNoMoreInteractions(avaliacaoGateway);
    }

    @Test
    @DisplayName("Deve enviar a avaliação correta para persistência")
    void deveEnviarAvaliacaoCorretaParaPersistencia() {

        // Arrange
        ArgumentCaptor<Avaliacao> captor =
                ArgumentCaptor.forClass(Avaliacao.class);

        when(avaliacaoGateway.save(any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        createAvaliacaoUseCase.execute(input);

        // Assert
        verify(avaliacaoGateway).save(captor.capture());

        Avaliacao avaliacaoEnviada = captor.getValue();

        assertNotNull(avaliacaoEnviada.getId());
        assertEquals(input.getDescricao(), avaliacaoEnviada.getDescricao());
        assertEquals(input.getNota(), avaliacaoEnviada.getNota());
        assertNotNull(avaliacaoEnviada.getData());
    }

    @Test
    @DisplayName("Deve retornar os dados efetivamente persistidos")
    void deveRetornarDadosPersistidos() {

        // Arrange
        UUID idPersistido = UUID.randomUUID();

        Avaliacao avaliacaoPersistida = Avaliacao.builder()
                .id(idPersistido)
                .descricao("Atendimento satisfatório")
                .nota(8)
                .build();

        when(avaliacaoGateway.save(any(Avaliacao.class)))
                .thenReturn(avaliacaoPersistida);

        // Act
        AvaliacaoOutput output = createAvaliacaoUseCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(idPersistido, output.getId());
        assertEquals("Atendimento satisfatório", output.getDescricao());
        assertEquals(8, output.getNota());
        assertEquals(avaliacaoPersistida.getData(), output.getData());

        verify(avaliacaoGateway, times(1))
                .save(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer falha na persistência")
    void devePropagarExcecaoQuandoFalharPersistencia() {

        // Arrange
        RuntimeException exception =
                new RuntimeException("Erro ao persistir avaliação");

        when(avaliacaoGateway.save(any(Avaliacao.class)))
                .thenThrow(exception);

        // Act
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createAvaliacaoUseCase.execute(input)
        );

        // Assert
        assertSame(exception, thrown);

        verify(avaliacaoGateway, times(1))
                .save(any(Avaliacao.class));

        verifyNoMoreInteractions(avaliacaoGateway);
    }
}
