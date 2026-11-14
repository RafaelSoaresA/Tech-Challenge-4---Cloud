
package br.com.techchallenge.avaliacao.presentation.avaliacao.conroller;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.dto.CreateAvaliacaoInput;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.CreateAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.DeleteByIdAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.FindByIdAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.ListAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.presentation.avaliacao.request.AvaliacaoRequest;
import br.com.techchallenge.avaliacao.presentation.avaliacao.response.AvaliacaoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AvaliacaoController")
class AvaliacaoControllerTest {

    @Mock
    private CreateAvaliacaoUseCase createAvaliacaoUseCase;

    @Mock
    private DeleteByIdAvaliacaoUseCase deleteByIdAvaliacaoUseCase;

    @Mock
    private ListAvaliacaoUseCase listAvaliacaoUseCase;

    @Mock
    private FindByIdAvaliacaoUseCase findByIdAvaliacaoUseCase;

    @InjectMocks
    private AvaliacaoController controller;

    private UUID id;
    private Instant data;
    private AvaliacaoRequest request;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        data = Instant.parse("2026-11-08T12:00:00Z");
        request = mock(AvaliacaoRequest.class);
    }

    private AvaliacaoOutput criarOutput() {
        AvaliacaoOutput output = mock(AvaliacaoOutput.class);

        when(output.getId()).thenReturn(id);
        when(output.getDescricao()).thenReturn("Excelente atendimento");
        when(output.getNota()).thenReturn(10);
        when(output.getData()).thenReturn(data);

        return output;
    }

    @Nested
    @DisplayName("Criação de avaliação")
    class CriarAvaliacao {

        @Test
        @DisplayName("Deve criar avaliação e retornar HTTP 200")
        void deveCriarAvaliacaoComSucesso() {
            // Arrange
            AvaliacaoOutput output = criarOutput();

            when(createAvaliacaoUseCase.execute(any(CreateAvaliacaoInput.class)))
                    .thenReturn(output);

            // Act
            ResponseEntity<AvaliacaoResponse> resultado =
                    controller.createAvaliacao(request);

            // Assert
            assertEquals(HttpStatus.OK, resultado.getStatusCode());
            assertNotNull(resultado.getBody());

            assertEquals(id, resultado.getBody().getId());
            assertEquals("Excelente atendimento", resultado.getBody().getDescricao());
            assertEquals(10, resultado.getBody().getNota());
            assertEquals(data, resultado.getBody().getData());

            verify(createAvaliacaoUseCase).execute(any(CreateAvaliacaoInput.class));
        }

        @Test
        @DisplayName("Deve propagar exceção quando a criação falhar")
        void devePropagarExcecaoQuandoCriacaoFalhar() {
            // Arrange
            RuntimeException excecao =
                    new RuntimeException("Erro ao criar avaliação");

            when(createAvaliacaoUseCase.execute(any(CreateAvaliacaoInput.class)))
                    .thenThrow(excecao);

            // Act
            RuntimeException resultado = assertThrows(
                    RuntimeException.class,
                    () -> controller.createAvaliacao(request)
            );

            // Assert
            assertSame(excecao, resultado);

            verify(createAvaliacaoUseCase).execute(any(CreateAvaliacaoInput.class));
        }
    }

    @Nested
    @DisplayName("Listagem de avaliações")
    class ListarAvaliacoes {

        @Test
        @DisplayName("Deve listar avaliações cadastradas")
        void deveListarAvaliacoesComSucesso() {
            // Arrange
            AvaliacaoOutput output = criarOutput();

            when(listAvaliacaoUseCase.execute())
                    .thenReturn(List.of(output));

            // Act
            ResponseEntity<List<AvaliacaoResponse>> resultado =
                    controller.listAvaliacao();

            // Assert
            assertEquals(HttpStatus.OK, resultado.getStatusCode());
            assertNotNull(resultado.getBody());
            assertEquals(1, resultado.getBody().size());

            AvaliacaoResponse avaliacaoResponse = resultado.getBody().get(0);

            assertEquals(id, avaliacaoResponse.getId());
            assertEquals("Excelente atendimento", avaliacaoResponse.getDescricao());
            assertEquals(10, avaliacaoResponse.getNota());
            assertEquals(data, avaliacaoResponse.getData());

            verify(listAvaliacaoUseCase).execute();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver avaliações")
        void deveRetornarListaVazia() {
            // Arrange
            when(listAvaliacaoUseCase.execute())
                    .thenReturn(List.of());

            // Act
            ResponseEntity<List<AvaliacaoResponse>> resultado =
                    controller.listAvaliacao();

            // Assert
            assertEquals(HttpStatus.OK, resultado.getStatusCode());
            assertNotNull(resultado.getBody());
            assertTrue(resultado.getBody().isEmpty());

            verify(listAvaliacaoUseCase).execute();
        }

        @Test
        @DisplayName("Deve propagar exceção quando a listagem falhar")
        void devePropagarExcecaoQuandoListagemFalhar() {
            // Arrange
            RuntimeException excecao =
                    new RuntimeException("Erro ao listar avaliações");

            when(listAvaliacaoUseCase.execute())
                    .thenThrow(excecao);

            // Act
            RuntimeException resultado = assertThrows(
                    RuntimeException.class,
                    () -> controller.listAvaliacao()
            );

            // Assert
            assertSame(excecao, resultado);

            verify(listAvaliacaoUseCase).execute();
        }
    }

    @Nested
    @DisplayName("Busca de avaliação por ID")
    class BuscarAvaliacao {

        @Test
        @DisplayName("Deve buscar avaliação por ID com sucesso")
        void deveBuscarAvaliacaoPorIdComSucesso() {
            // Arrange
            AvaliacaoOutput output = criarOutput();

            when(findByIdAvaliacaoUseCase.execute(id))
                    .thenReturn(output);

            // Act
            ResponseEntity<AvaliacaoResponse> resultado =
                    controller.findById(id);

            // Assert
            assertEquals(HttpStatus.OK, resultado.getStatusCode());
            assertNotNull(resultado.getBody());

            assertEquals(id, resultado.getBody().getId());
            assertEquals("Excelente atendimento", resultado.getBody().getDescricao());
            assertEquals(10, resultado.getBody().getNota());
            assertEquals(data, resultado.getBody().getData());

            verify(findByIdAvaliacaoUseCase).execute(id);
        }

        @Test
        @DisplayName("Deve propagar exceção quando a avaliação não for encontrada")
        void devePropagarExcecaoQuandoAvaliacaoNaoExistir() {
            // Arrange
            RuntimeException excecao =
                    new RuntimeException("Avaliação não encontrada");

            when(findByIdAvaliacaoUseCase.execute(id))
                    .thenThrow(excecao);

            // Act
            RuntimeException resultado = assertThrows(
                    RuntimeException.class,
                    () -> controller.findById(id)
            );

            // Assert
            assertSame(excecao, resultado);

            verify(findByIdAvaliacaoUseCase).execute(id);
        }
    }

    @Nested
    @DisplayName("Exclusão de avaliação")
    class ExcluirAvaliacao {

        @Test
        @DisplayName("Deve excluir avaliação e retornar HTTP 204")
        void deveExcluirAvaliacaoComSucesso() {
            // Act
            ResponseEntity<AvaliacaoResponse> resultado =
                    controller.deleteAvaliacao(id);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
            assertNull(resultado.getBody());

            verify(deleteByIdAvaliacaoUseCase).execute(id);
        }

        @Test
        @DisplayName("Deve propagar exceção quando a exclusão falhar")
        void devePropagarExcecaoQuandoExclusaoFalhar() {
            // Arrange
            RuntimeException excecao =
                    new RuntimeException("Erro ao excluir avaliação");

            doThrow(excecao)
                    .when(deleteByIdAvaliacaoUseCase)
                    .execute(id);

            // Act
            RuntimeException resultado = assertThrows(
                    RuntimeException.class,
                    () -> controller.deleteAvaliacao(id)
            );

            // Assert
            assertSame(excecao, resultado);

            verify(deleteByIdAvaliacaoUseCase).execute(id);
        }
    }
}
