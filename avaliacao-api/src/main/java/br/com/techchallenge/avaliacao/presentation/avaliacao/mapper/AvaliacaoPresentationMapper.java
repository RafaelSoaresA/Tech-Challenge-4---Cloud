package br.com.techchallenge.avaliacao.presentation.avaliacao.mapper;

import br.com.techchallenge.avaliacao.application.avaliacao.dto.AvaliacaoOutput;
import br.com.techchallenge.avaliacao.application.avaliacao.dto.CreateAvaliacaoInput;
import br.com.techchallenge.avaliacao.presentation.avaliacao.request.AvaliacaoRequest;
import br.com.techchallenge.avaliacao.presentation.avaliacao.response.AvaliacaoResponse;

public class AvaliacaoPresentationMapper {

    public static CreateAvaliacaoInput toInput(AvaliacaoRequest request){
        return CreateAvaliacaoInput.builder()
                .descricao(request.getDescricao())
                .nota(request.getNota())
                .build();
    }

    public static AvaliacaoResponse toResponse(AvaliacaoOutput output){
        return new AvaliacaoResponse(
                output.getId(),
                output.getDescricao(),
                output.getNota(),
                output.getData()
        );
    }

}
