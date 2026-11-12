package br.com.techchallenge.feedback.notificacoes.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorUrgenciaTest {

    @Test
    void notaNoLimiteEhHigh() {
        assertEquals(Urgencia.HIGH, ClassificadorUrgencia.classificar(4, 4));
    }

    @Test
    void notaAcimaDoLimiteEAbaixoDeSeteEhMedium() {
        assertEquals(Urgencia.MEDIUM, ClassificadorUrgencia.classificar(5, 4));
        assertEquals(Urgencia.MEDIUM, ClassificadorUrgencia.classificar(6.9, 4));
    }

    @Test
    void notaASeteEhLow() {
        assertEquals(Urgencia.LOW, ClassificadorUrgencia.classificar(7, 4));
        assertEquals(Urgencia.LOW, ClassificadorUrgencia.classificar(10, 4));
    }

    @Test
    void notaZeroEhHigh() {
        assertEquals(Urgencia.HIGH, ClassificadorUrgencia.classificar(0, 4));
    }
}
