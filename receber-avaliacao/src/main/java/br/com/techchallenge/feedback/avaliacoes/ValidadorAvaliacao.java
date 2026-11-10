package br.com.techchallenge.feedback.avaliacoes;

import java.util.Optional;

/** Regras de validação da US01: descrição obrigatória e nota entre 0 e 10. */
public final class ValidadorAvaliacao {

    private ValidadorAvaliacao() {
    }

    /** @return mensagem de erro, ou vazio se a avaliação é válida. */
    public static Optional<String> validar(AvaliacaoRequest req) {
        if (req == null) {
            return Optional.of("Corpo da requisição é obrigatório");
        }
        if (req.descricao() == null || req.descricao().isBlank()) {
            return Optional.of("Campo 'descricao' é obrigatório");
        }
        if (req.nota() == null) {
            return Optional.of("Campo 'nota' é obrigatório");
        }
        if (req.nota().isNaN() || req.nota() < 0 || req.nota() > 10) {
            return Optional.of("Campo 'nota' deve estar entre 0 e 10");
        }
        return Optional.empty();
    }
}
