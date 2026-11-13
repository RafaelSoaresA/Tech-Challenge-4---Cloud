package br.com.techchallenge.avaliacao.presentation.avaliacao.exception.handle;

import br.com.techchallenge.avaliacao.domain.avaliacao.exception.*;
import br.com.techchallenge.avaliacao.presentation.avaliacao.exception.ProblemDetailFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalAuthExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Os dados informados são inválidos.",
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleInvalidRequestBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_BODY",
                "O corpo da requisição é inválido ou está malformado.",
                "Verifique o formato e os valores enviados.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "MISSING_REQUEST_PARAMETER",
                "Um parâmetro obrigatório não foi informado.",
                "Parâmetro ausente: " + ex.getParameterName(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleResourceNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "O recurso solicitado não foi encontrado.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "O método HTTP utilizado não é permitido para este recurso.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "O formato de mídia enviado não é suportado.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternalServerError(
            Exception ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Ocorreu um erro interno ao processar a solicitação.",
                "Não foi possível concluir a operação. Tente novamente mais tarde.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AvaliacaoNotFoundException.class)
    public ProblemDetail handleAvaliacaoNotFound(
            AvaliacaoNotFoundException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "AVALIACAO_NAO_ENCONTRADA",
                "Avaliação não encontrada.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DescricaoAvaliacaoExcedeLimiteException.class)
    public ProblemDetail handleDescricaoAvaliacaoExcedeLimite(
            DescricaoAvaliacaoExcedeLimiteException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "DESCRICAO_AVALIACAO_EXCEDE_LIMITE",
                "A descrição da avaliação excede o limite permitido.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DescricaoAvaliacaoObrigatoriaException.class)
    public ProblemDetail handleDescricaoAvaliacaoObrigatoria(
            DescricaoAvaliacaoObrigatoriaException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "DESCRICAO_AVALIACAO_OBRIGATORIA",
                "A descrição da avaliação é obrigatória.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotaAvaliacaoInvalidaException.class)
    public ProblemDetail handleNotaAvaliacaoInvalida(
            NotaAvaliacaoInvalidaException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "NOTA_AVALIACAO_INVALIDA",
                "A nota informada é inválida.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotaAvaliacaoObrigatoriaException.class)
    public ProblemDetail handleNotaAvaliacaoObrigatoria(
            NotaAvaliacaoObrigatoriaException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "NOTA_AVALIACAO_OBRIGATORIA",
                "A nota da avaliação é obrigatória.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}
