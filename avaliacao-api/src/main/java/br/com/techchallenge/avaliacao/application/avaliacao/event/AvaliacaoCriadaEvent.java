package br.com.techchallenge.avaliacao.application.avaliacao.event;

import java.time.Instant;
import java.util.UUID;

public record AvaliacaoCriadaEvent(
        String eventType,
        Instant occurredAt,
        Data data
) {
    public record Data(
            UUID id,
            String descricao,
            Integer nota
    ) {}
}