package br.com.techchallenge.avaliacao.presentation.avaliacao.conroller;

import br.com.techchallenge.avaliacao.application.avaliacao.usecase.CreateAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.DeleteByIdAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.FindByIdAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.application.avaliacao.usecase.ListAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.presentation.avaliacao.mapper.AvaliacaoPresentationMapper;
import br.com.techchallenge.avaliacao.presentation.avaliacao.request.AvaliacaoRequest;
import br.com.techchallenge.avaliacao.presentation.avaliacao.response.AvaliacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacao/v1")
public class AvaliacaoController {

    private final CreateAvaliacaoUseCase createAvaliacaoUseCase;
    private final DeleteByIdAvaliacaoUseCase deleteByIdAvaliacaoUseCase;
    private final ListAvaliacaoUseCase listAvaliacaoUseCase;
    private final FindByIdAvaliacaoUseCase findByIdAvaliacaoUseCase;


    public AvaliacaoController(CreateAvaliacaoUseCase createAvaliacaoUseCase, DeleteByIdAvaliacaoUseCase deleteByIdAvaliacaoUseCase, ListAvaliacaoUseCase listAvaliacaoUseCase, FindByIdAvaliacaoUseCase findByIdAvaliacaoUseCase) {
        this.createAvaliacaoUseCase = createAvaliacaoUseCase;
        this.deleteByIdAvaliacaoUseCase = deleteByIdAvaliacaoUseCase;
        this.listAvaliacaoUseCase = listAvaliacaoUseCase;
        this.findByIdAvaliacaoUseCase = findByIdAvaliacaoUseCase;
    }


    @PostMapping
    public ResponseEntity<AvaliacaoResponse>  createAvaliacao(@Valid @RequestBody AvaliacaoRequest avaliacaoRequest) {
        var input = AvaliacaoPresentationMapper.toInput(avaliacaoRequest);
        var output =  createAvaliacaoUseCase.execute(input);
        var response = AvaliacaoPresentationMapper.toResponse(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponse>> listAvaliacao() {
        var response = listAvaliacaoUseCase.execute().stream()
                .map(AvaliacaoPresentationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponse> findById(@PathVariable UUID id) {
        var response = findByIdAvaliacaoUseCase.execute(id);

        return ResponseEntity.ok(AvaliacaoPresentationMapper.toResponse(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AvaliacaoResponse> deleteAvaliacao(@PathVariable UUID id) {
        deleteByIdAvaliacaoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
