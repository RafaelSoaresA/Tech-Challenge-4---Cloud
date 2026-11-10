package br.com.techchallenge.feedback.relatorios;

import java.util.List;
import java.util.Map;

/** Conteúdo do relatório semanal gravado no S3 (serializado em JSON). */
public record Relatorio(
        String geradoEm,
        int totalAvaliacoes,
        Double mediaNotas,
        Map<String, Long> avaliacoesPorDia,
        Map<String, Long> avaliacoesPorUrgencia,
        List<ItemRelatorio> avaliacoes) {

    public record ItemRelatorio(String descricao, String urgencia, String dataEnvio) {
    }
}
