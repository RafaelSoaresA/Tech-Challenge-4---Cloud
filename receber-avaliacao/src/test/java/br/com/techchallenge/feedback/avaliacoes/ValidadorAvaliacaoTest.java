package br.com.techchallenge.feedback.avaliacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidadorAvaliacaoTest {

    @Test
    void aceitaAvaliacaoValida() {
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("Aula boa", 8.0)).isEmpty());
    }

    @Test
    void aceitaLimites() {
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("x", 0.0)).isEmpty());
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("x", 10.0)).isEmpty());
    }

    @Test
    void rejeitaNotaForaDoIntervalo() {
        assertEquals("Campo 'nota' deve estar entre 0 e 10",
                ValidadorAvaliacao.validar(new AvaliacaoRequest("x", 11.0)).orElseThrow());
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("x", -1.0)).isPresent());
    }

    @Test
    void rejeitaSemDescricaoOuSemNota() {
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest(null, 5.0)).isPresent());
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("  ", 5.0)).isPresent());
        assertTrue(ValidadorAvaliacao.validar(new AvaliacaoRequest("x", null)).isPresent());
        assertTrue(ValidadorAvaliacao.validar(null).isPresent());
    }
}
