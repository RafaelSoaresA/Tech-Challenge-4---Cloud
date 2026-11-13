
package br.com.techchallenge.avaliacao.domain.avaliacao.entiy;

import br.com.techchallenge.avaliacao.domain.avaliacao.exception.DescricaoAvaliacaoExcedeLimiteException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.DescricaoAvaliacaoObrigatoriaException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.NotaAvaliacaoInvalidaException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.NotaAvaliacaoObrigatoriaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Avaliacao")
class AvaliacaoTest {

    @Nested
    @DisplayName("Construção da avaliação")
    class Builder {

        @Test
        @DisplayName("Deve construir avaliação com dados válidos")
        void deveConstruirAvaliacaoComDadosValidos() {

            // Arrange
            UUID id = UUID.randomUUID();

            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .id(id)
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Assert
            assertNotNull(avaliacao);
            assertEquals(id, avaliacao.getId());
            assertEquals("Excelente atendimento", avaliacao.getDescricao());
            assertEquals(10, avaliacao.getNota());
            assertNotNull(avaliacao.getData());
        }

        @Test
        @DisplayName("Deve gerar UUID automaticamente quando não informado")
        void deveGerarUuidAutomaticamente() {

            // Arrange
            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(9)
                    .build();

            // Assert
            assertNotNull(avaliacao.getId());
        }

        @Test
        @DisplayName("Deve gerar UUIDs diferentes para avaliações distintas")
        void deveGerarUuidsDiferentes() {

            // Arrange
            // Act
            Avaliacao primeira = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(9)
                    .build();

            Avaliacao segunda = Avaliacao.builder()
                    .descricao("Bom atendimento")
                    .nota(8)
                    .build();

            // Assert
            assertNotEquals(primeira.getId(), segunda.getId());
        }

        @Test
        @DisplayName("Deve gerar data automaticamente na criação")
        void deveGerarDataAutomaticamente() {

            // Arrange
            Instant inicio = Instant.now();

            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            Instant fim = Instant.now();

            // Assert
            assertNotNull(avaliacao.getData());
            assertFalse(avaliacao.getData().isBefore(inicio));
            assertFalse(avaliacao.getData().isAfter(fim));
        }
    }

    @Nested
    @DisplayName("Validação da descrição")
    class ValidacaoDescricao {

        @Test
        @DisplayName("Deve rejeitar descrição nula")
        void deveRejeitarDescricaoNula() {

            // Arrange
            // Act & Assert
            assertThrows(
                    DescricaoAvaliacaoObrigatoriaException.class,
                    () -> Avaliacao.builder()
                            .descricao(null)
                            .nota(10)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve rejeitar descrição vazia")
        void deveRejeitarDescricaoVazia() {

            // Arrange
            // Act & Assert
            assertThrows(
                    DescricaoAvaliacaoObrigatoriaException.class,
                    () -> Avaliacao.builder()
                            .descricao("")
                            .nota(10)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve rejeitar descrição contendo apenas espaços")
        void deveRejeitarDescricaoComEspacos() {

            // Arrange
            // Act & Assert
            assertThrows(
                    DescricaoAvaliacaoObrigatoriaException.class,
                    () -> Avaliacao.builder()
                            .descricao("   ")
                            .nota(10)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve aceitar descrição com exatamente 2000 caracteres")
        void deveAceitarDescricaoComLimiteMaximo() {

            // Arrange
            String descricao = "A".repeat(2000);

            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao(descricao)
                    .nota(10)
                    .build();

            // Assert
            assertNotNull(avaliacao);
            assertEquals(2000, avaliacao.getDescricao().length());
        }

        @Test
        @DisplayName("Deve rejeitar descrição com mais de 2000 caracteres")
        void deveRejeitarDescricaoAcimaDoLimite() {

            // Arrange
            String descricao = "A".repeat(2001);

            // Act & Assert
            assertThrows(
                    DescricaoAvaliacaoExcedeLimiteException.class,
                    () -> Avaliacao.builder()
                            .descricao(descricao)
                            .nota(10)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Validação da nota")
    class ValidacaoNota {

        @Test
        @DisplayName("Deve rejeitar nota nula")
        void deveRejeitarNotaNula() {

            // Arrange
            // Act & Assert
            assertThrows(
                    NotaAvaliacaoObrigatoriaException.class,
                    () -> Avaliacao.builder()
                            .descricao("Excelente atendimento")
                            .nota(null)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve rejeitar nota inferior a zero")
        void deveRejeitarNotaInferiorAZero() {

            // Arrange
            // Act & Assert
            assertThrows(
                    NotaAvaliacaoInvalidaException.class,
                    () -> Avaliacao.builder()
                            .descricao("Excelente atendimento")
                            .nota(-1)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve rejeitar nota superior a dez")
        void deveRejeitarNotaSuperiorADez() {

            // Arrange
            // Act & Assert
            assertThrows(
                    NotaAvaliacaoInvalidaException.class,
                    () -> Avaliacao.builder()
                            .descricao("Excelente atendimento")
                            .nota(11)
                            .build()
            );
        }

        @Test
        @DisplayName("Deve aceitar nota mínima igual a zero")
        void deveAceitarNotaMinima() {

            // Arrange
            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Atendimento")
                    .nota(0)
                    .build();

            // Assert
            assertEquals(0, avaliacao.getNota());
        }

        @Test
        @DisplayName("Deve aceitar nota máxima igual a dez")
        void deveAceitarNotaMaxima() {

            // Arrange
            // Act
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Assert
            assertEquals(10, avaliacao.getNota());
        }
    }

    @Nested
    @DisplayName("Identidade da avaliação")
    class Identidade {

        @Test
        @DisplayName("Deve considerar avaliações com mesmo UUID como iguais")
        void deveConsiderarAvaliacoesComMesmoIdComoIguais() {

            // Arrange
            UUID id = UUID.randomUUID();

            Avaliacao primeira = Avaliacao.builder()
                    .id(id)
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            Avaliacao segunda = Avaliacao.builder()
                    .id(id)
                    .descricao("Atendimento razoável")
                    .nota(5)
                    .build();

            // Act & Assert
            assertEquals(primeira, segunda);
        }

        @Test
        @DisplayName("Deve considerar avaliações com UUIDs diferentes como diferentes")
        void deveConsiderarAvaliacoesComIdsDiferentesComoDiferentes() {

            // Arrange
            Avaliacao primeira = Avaliacao.builder()
                    .id(UUID.randomUUID())
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            Avaliacao segunda = Avaliacao.builder()
                    .id(UUID.randomUUID())
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Act & Assert
            assertNotEquals(primeira, segunda);
        }

        @Test
        @DisplayName("Deve considerar a mesma instância como igual")
        void deveConsiderarMesmaInstanciaComoIgual() {

            // Arrange
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Act & Assert
            assertEquals(avaliacao, avaliacao);
        }

        @Test
        @DisplayName("Não deve considerar objeto nulo como igual")
        void naoDeveConsiderarObjetoNuloComoIgual() {

            // Arrange
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Act & Assert
            assertNotEquals(null, avaliacao);
        }

        @Test
        @DisplayName("Deve considerar objetos de classes diferentes como diferentes")
        void deveConsiderarClassesDiferentesComoDiferentes() {

            // Arrange
            Avaliacao avaliacao = Avaliacao.builder()
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            // Act & Assert
            assertNotEquals(avaliacao, "Excelente atendimento");
        }

        @Test
        @DisplayName("Deve gerar o mesmo hashCode para avaliações com mesmo UUID")
        void deveGerarMesmoHashCodeParaMesmoId() {

            // Arrange
            UUID id = UUID.randomUUID();

            Avaliacao primeira = Avaliacao.builder()
                    .id(id)
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            Avaliacao segunda = Avaliacao.builder()
                    .id(id)
                    .descricao("Bom atendimento")
                    .nota(8)
                    .build();

            // Act & Assert
            assertEquals(primeira.hashCode(), segunda.hashCode());
        }

        @Test
        @DisplayName("Deve gerar hashCode diferente para UUIDs diferentes")
        void deveGerarHashCodeDiferenteParaIdsDiferentes() {

            // Arrange
            Avaliacao primeira = Avaliacao.builder()
                    .id(UUID.randomUUID())
                    .descricao("Excelente atendimento")
                    .nota(10)
                    .build();

            Avaliacao segunda = Avaliacao.builder()
                    .id(UUID.randomUUID())
                    .descricao("Bom atendimento")
                    .nota(8)
                    .build();

            // Act & Assert
            assertNotEquals(primeira.hashCode(), segunda.hashCode());
        }
    }
}
