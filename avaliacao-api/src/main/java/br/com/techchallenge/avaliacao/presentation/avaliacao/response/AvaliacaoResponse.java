package br.com.techchallenge.avaliacao.presentation.avaliacao.response;

import java.time.Instant;
import java.util.UUID;

public class AvaliacaoResponse {

    private UUID id;
    private String descricao;
    private Integer nota;
    private Instant dataEnvio;

    public AvaliacaoResponse(UUID id, String descricao, Integer nota, Instant dataEnvio) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.dataEnvio = dataEnvio;
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public Instant getDataEnvio() {
        return dataEnvio;
    }
}
