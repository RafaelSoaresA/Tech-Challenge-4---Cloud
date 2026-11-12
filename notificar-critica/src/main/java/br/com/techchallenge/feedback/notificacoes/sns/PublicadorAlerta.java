package br.com.techchallenge.feedback.notificacoes.sns;

import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoEvento;
import br.com.techchallenge.feedback.notificacoes.domain.Urgencia;

import java.time.Instant;

public interface PublicadorAlerta {

    void publicar(AvaliacaoEvento avaliacao, Urgencia urgencia, Instant enviadoEm);
}
