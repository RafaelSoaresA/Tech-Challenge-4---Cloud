package br.com.techchallenge.avaliacao.application.avaliacao.mapper;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.dto.CreateAvaliacaoInput;
import br.com.techchallenge.avaliacao.domain.avaliacao.entiy.Avaliacao;

public class AvaliacaoApplicationMapper {

    public static Avaliacao toDomain(CreateAvaliacaoInput input){
        return Avaliacao.builder()
                .descricao(input.getDescricao())
                .nota(input.getNota())
                .build();
    }

    public static AvaliacaoOutput toOuput(Avaliacao avaliacao){
        return AvaliacaoOutput.builder()
                .id(avaliacao.getId())
                .descricao(avaliacao.getDescricao())
                .nota(avaliacao.getNota())
                .data(avaliacao.getData())
                .build();
    }

}
