package br.com.techchallenge.avaliacao.application.avaliacao.dto;

import java.util.UUID;

public class CreateAvaliacaoInput {

    private UUID id;
    private String descricao;
    private Integer nota;


    public CreateAvaliacaoInput(Builder builder) {
        this.id = builder.id;
        this.descricao = builder.descricao;
        this.nota = builder.nota;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private UUID id;
        private String descricao;
        private Integer nota;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder nota(Integer nota) {
            this.nota = nota;
            return this;
        }

        public CreateAvaliacaoInput build() {
            return new CreateAvaliacaoInput(this);
        }
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
}
