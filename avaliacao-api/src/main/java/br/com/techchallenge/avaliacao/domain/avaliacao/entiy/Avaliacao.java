package br.com.techchallenge.avaliacao.domain.avaliacao.entiy;

import br.com.techchallenge.avaliacao.domain.avaliacao.exception.DescricaoAvaliacaoExcedeLimiteException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.DescricaoAvaliacaoObrigatoriaException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.NotaAvaliacaoInvalidaException;
import br.com.techchallenge.avaliacao.domain.avaliacao.exception.NotaAvaliacaoObrigatoriaException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Avaliacao {

    private UUID id;
    private String descricao;
    private Integer nota;
    private Instant data;


    private Avaliacao(Builder builder) {

        validarDescricao(builder.descricao);
        validarNota(builder.nota);


        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.descricao = builder.descricao;
        this.nota = builder.nota;
        this.data = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private UUID id;
        private String descricao;
        private Integer nota;

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


        public Avaliacao build(){
            return new Avaliacao(this);
        }

    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new DescricaoAvaliacaoObrigatoriaException();
        }

        if (descricao.length() > 2000) {
            throw new DescricaoAvaliacaoExcedeLimiteException();
        }
    }

    private void validarNota(Integer nota) {
        if (nota == null) {
            throw new NotaAvaliacaoObrigatoriaException();
        }

        if (nota < 0 || nota > 10) {
            throw new NotaAvaliacaoInvalidaException();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
