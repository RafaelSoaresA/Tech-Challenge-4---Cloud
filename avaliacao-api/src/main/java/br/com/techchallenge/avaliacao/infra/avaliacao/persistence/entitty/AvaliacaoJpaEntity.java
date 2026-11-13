package br.com.techchallenge.avaliacao.infra.avaliacao.persistence.entitty;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AvaliacaoJpaEntity {

    private String id;
    private String descricao;
    private Integer nota;
    private String data;

    public AvaliacaoJpaEntity() {
    }

    public AvaliacaoJpaEntity(String descricao, Integer nota, String data) {
        this.descricao = descricao;
        this.nota = nota;
        this.data = data;
    }

    public AvaliacaoJpaEntity(String id, String descricao, Integer nota, String data) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.data = data;
    }

    @DynamoDbAttribute("id")
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("descricao")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @DynamoDbAttribute("nota")
    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    @DynamoDbAttribute("data")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
