package br.com.techchallenge.avaliacao.application.avaliacao.dto;

import java.time.Instant;
import java.util.UUID;

public class AvaliacaoOutput {

    private UUID id;
    private String descricao;
    private Integer nota;
    private Instant data;

    private AvaliacaoOutput(Builder builder) {


        this.id = builder.id;
        this.descricao = builder.descricao;
        this.nota = builder.nota;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private UUID id;
        private String descricao;
        private Integer nota;
        private Instant data;

        public Builder id(UUID id){
            this.id = id;
            return this;
        }

        public Builder descricao(String descricao){
            this.descricao = descricao;
            return this;
        }

        public Builder nota(Integer nota){
            this.nota = nota;
            return this;
        }

        public Builder data(Instant data){
            this.data = data;
            return this;
        }

        public AvaliacaoOutput build(){
            return new AvaliacaoOutput(this);
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

    public Instant getData() {
        return data;
    }
}
