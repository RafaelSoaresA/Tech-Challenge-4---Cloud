package br.com.techchallenge.feedback.notificacoes;

/** Regra de urgência (US04): nota menor ou igual ao limite é CRÍTICA. */
public final class ClassificadorUrgencia {

    private ClassificadorUrgencia() {
    }

    public static boolean ehCritica(double nota, double limite) {
        return nota <= limite;
    }

    public static String montarMensagem(String descricao, double nota, String dataEnvio) {
        return "Avaliação CRÍTICA recebida\n\n"
                + "Descrição: " + descricao + "\n"
                + "Nota: " + nota + "\n"
                + "Urgência: CRÍTICA\n"
                + "Data de envio: " + dataEnvio + "\n";
    }
}
