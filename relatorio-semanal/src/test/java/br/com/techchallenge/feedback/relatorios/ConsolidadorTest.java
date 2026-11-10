package br.com.techchallenge.feedback.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConsolidadorTest {

    @Test
    void consolidaMediaPorDiaEPorUrgencia() {
        var itens = List.of(
                new Avaliacao("a", 2, "2026-11-02T10:00:00Z"),
                new Avaliacao("b", 8, "2026-11-02T12:00:00Z"),
                new Avaliacao("c", 10, "2026-11-03T09:00:00Z"));

        var r = Consolidador.consolidar(itens, 4, "2026-11-09T08:00:00Z");

        assertEquals(3, r.totalAvaliacoes());
        assertEquals(6.67, r.mediaNotas());
        assertEquals(Map.of("2026-11-02", 2L, "2026-11-03", 1L), r.avaliacoesPorDia());
        assertEquals(Map.of("CRITICA", 1L, "NORMAL", 2L), r.avaliacoesPorUrgencia());
        assertEquals("CRITICA", r.avaliacoes().get(0).urgencia());
    }

    @Test
    void semAvaliacoesNaoTemMedia() {
        var r = Consolidador.consolidar(List.of(), 4, "2026-11-09T08:00:00Z");
        assertEquals(0, r.totalAvaliacoes());
        assertNull(r.mediaNotas());
    }
}
