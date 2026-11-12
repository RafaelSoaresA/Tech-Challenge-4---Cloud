package br.com.techchallenge.feedback.notificacoes.domain;

/** Regra de urgência (US04): nota menor ou igual ao limite é CRÍTICA. */
public final class ClassificadorUrgencia {

    private ClassificadorUrgencia() {
    }

    public static Urgencia classificar(double nota, double limite) {
        return nota <= limite ? Urgencia.CRITICA : Urgencia.NORMAL;
    }
}
