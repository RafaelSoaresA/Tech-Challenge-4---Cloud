package br.com.techchallenge.avaliacao.presentation.avaliacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;

public class ProblemDetailFactory {
    private static final String BASE_TYPE_URL = "https://api.avaliacao-plataform.com/errors/";

    public static ProblemDetail create(
            HttpStatus status,
            String code,
            String title,
            String detail,
            String path
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(status);

        // RFC 7807 padrão
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setInstance(URI.create(path));
        pd.setType(URI.create(BASE_TYPE_URL + code));

        pd.setProperty("code", code);
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }
}
