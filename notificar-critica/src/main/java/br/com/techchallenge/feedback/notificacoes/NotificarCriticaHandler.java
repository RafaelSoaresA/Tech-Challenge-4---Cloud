package br.com.techchallenge.feedback.notificacoes;

import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoEvento;
import br.com.techchallenge.feedback.notificacoes.domain.AvaliacaoMapper;
import br.com.techchallenge.feedback.notificacoes.domain.ClassificadorUrgencia;
import br.com.techchallenge.feedback.notificacoes.domain.Urgencia;
import br.com.techchallenge.feedback.notificacoes.sns.PublicadorAlerta;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lambda 2 — disparada pelo DynamoDB Stream a cada avaliação inserida (US03).
 * Classifica a urgência (US04) e publica alerta no SNS (US06).
 */
@ApplicationScoped
@Named("notificar-critica")
public class NotificarCriticaHandler implements RequestHandler<DynamodbEvent, Map<String, Integer>> {

    private static final Logger LOG = Logger.getLogger(NotificarCriticaHandler.class);

    private final PublicadorAlerta publicador;
    private final double limiteNotaCritica;

    @Inject
    public NotificarCriticaHandler(
            PublicadorAlerta publicador,
            @ConfigProperty(name = "limite.nota.critica", defaultValue = "4") double limiteNotaCritica) {
        this.publicador = publicador;
        this.limiteNotaCritica = limiteNotaCritica;
    }

    @Override
    public Map<String, Integer> handleRequest(DynamodbEvent evento, Context contexto) {
        int processados = 0;
        int alertasEnviados = 0;
        int ignorados = 0;
        List<String> falhas = new ArrayList<>();

        List<DynamodbEvent.DynamodbStreamRecord> registros = evento == null || evento.getRecords() == null
                ? List.of()
                : evento.getRecords();

        for (DynamodbEvent.DynamodbStreamRecord registro : registros) {
            if (registro.getEventName() != null && !"INSERT".equals(registro.getEventName())) {
                ignorados++;
                continue;
            }

            Optional<AvaliacaoEvento> extraida = AvaliacaoMapper.extrair(registro);
            if (extraida.isEmpty()) {
                ignorados++;
                LOG.warnf("Registro sem nota valida ignorado: eventID=%s", registro.getEventID());
                continue;
            }

            AvaliacaoEvento avaliacao = extraida.get();
            processados++;

            Urgencia urgencia = ClassificadorUrgencia.classificar(avaliacao.nota(), limiteNotaCritica);
            if (urgencia != Urgencia.CRITICA) {
                LOG.infof("Avaliacao %s nao critica (nota=%s); alerta nao enviado",
                        avaliacao.id(), avaliacao.nota());
                continue;
            }

            try {
                publicador.publicar(avaliacao, urgencia, Instant.now());
                alertasEnviados++;
                LOG.infof("Alerta enviado: avaliacao=%s nota=%s urgencia=%s",
                        avaliacao.id(), avaliacao.nota(), urgencia);
            } catch (RuntimeException excecao) {
                falhas.add(registro.getEventID());
                LOG.errorf(excecao, "Falha ao publicar alerta: avaliacao=%s", avaliacao.id());
            }
        }

        // Se falhar, a exceção sobe e o Lambda reprocessa o lote (MaximumRetryAttempts no template)
        if (!falhas.isEmpty()) {
            throw new IllegalStateException("Falha ao publicar " + falhas.size()
                    + " alerta(s); lote sera reprocessado: " + falhas);
        }

        Map<String, Integer> resultado = Map.of(
                "processados", processados,
                "alertasEnviados", alertasEnviados,
                "ignorados", ignorados);

        LOG.infof("Execucao concluida: %s", resultado);
        return resultado;
    }
}
