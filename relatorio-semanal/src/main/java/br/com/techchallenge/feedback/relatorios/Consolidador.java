package br.com.techchallenge.feedback.relatorios;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Cálculo dos indicadores (US07): média, contagem por dia e por urgência. */
public final class Consolidador {

    private Consolidador() {
    }

    public static String urgencia(double nota, double limite) {
        return nota <= limite ? "CRITICA" : "NORMAL";
    }

    public static Relatorio consolidar(List<Avaliacao> itens, double limite, String geradoEm) {
        Double media = null;
        if (!itens.isEmpty()) {
            double soma = itens.stream().mapToDouble(Avaliacao::nota).sum();
            media = Math.round(soma / itens.size() * 100.0) / 100.0;
        }

        Map<String, Long> porDia = new TreeMap<>();
        Map<String, Long> porUrgencia = new LinkedHashMap<>();
        for (var a : itens) {
            porDia.merge(a.dataEnvio().substring(0, 10), 1L, Long::sum);
            porUrgencia.merge(urgencia(a.nota(), limite), 1L, Long::sum);
        }

        var detalhes = itens.stream()
                .map(a -> new Relatorio.ItemRelatorio(a.descricao(), urgencia(a.nota(), limite), a.dataEnvio()))
                .toList();

        return new Relatorio(geradoEm, itens.size(), media, porDia, porUrgencia, detalhes);
    }
}
