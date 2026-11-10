package br.com.techchallenge.feedback.notificacoes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClassificadorUrgenciaTest {

    @Test
    void notaAbaixoOuIgualAoLimiteEhCritica() {
        assertTrue(ClassificadorUrgencia.ehCritica(2, 4));
        assertTrue(ClassificadorUrgencia.ehCritica(4, 4));
    }

    @Test
    void notaAcimaDoLimiteNaoDisparaAlerta() {
        assertFalse(ClassificadorUrgencia.ehCritica(4.1, 4));
        assertFalse(ClassificadorUrgencia.ehCritica(10, 4));
    }

    @Test
    void mensagemTrazDescricaoUrgenciaEData() {
        var msg = ClassificadorUrgencia.montarMensagem("Aula confusa", 2.0, "2026-11-10T20:00:00Z");
        assertTrue(msg.contains("Aula confusa"));
        assertTrue(msg.contains("CRÍTICA"));
        assertTrue(msg.contains("2026-11-10T20:00:00Z"));
    }
}
