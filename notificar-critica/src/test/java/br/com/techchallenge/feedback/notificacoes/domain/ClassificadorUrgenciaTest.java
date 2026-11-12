package br.com.techchallenge.feedback.notificacoes.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorUrgenciaTest {

    @Test
    void notaNoLimiteEhCritica() {
        assertEquals(Urgencia.CRITICA, ClassificadorUrgencia.classificar(4, 4));
    }

    @Test
    void notaAcimaDoLimiteEhNormal() {
        assertEquals(Urgencia.NORMAL, ClassificadorUrgencia.classificar(4.1, 4));
    }

    @Test
    void notaZeroEhCritica() {
        assertEquals(Urgencia.CRITICA, ClassificadorUrgencia.classificar(0, 4));
    }
}
