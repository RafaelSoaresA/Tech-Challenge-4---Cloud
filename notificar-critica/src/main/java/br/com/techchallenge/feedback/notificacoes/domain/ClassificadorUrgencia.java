package br.com.techchallenge.feedback.notificacoes.domain;

/**
 * Regra de urgência (US04) alinhada ao relatorio-semanal:
 * HIGH quando a nota é menor ou igual ao limite crítico (padrão 4),
 * MEDIUM abaixo de 7 e LOW a partir de 7.
 * Apenas HIGH gera alerta no SNS.
 */
public final class ClassificadorUrgencia {

    private static final double LIMITE_MEDIUM = 7;

    private ClassificadorUrgencia() {
    }

    public static Urgencia classificar(double nota, double limite) {
        if (nota <= limite) {
            return Urgencia.HIGH;
        }
        return nota < LIMITE_MEDIUM ? Urgencia.MEDIUM : Urgencia.LOW;
    }
}
