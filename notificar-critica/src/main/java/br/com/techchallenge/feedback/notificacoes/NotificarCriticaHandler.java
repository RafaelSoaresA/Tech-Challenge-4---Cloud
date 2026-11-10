package br.com.techchallenge.feedback.notificacoes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * ESQUELETO da Lambda 2 (Dev 2 evolui nas US04/US05/US06).
 * Disparada pelo DynamoDB Stream a cada avaliação inserida (evento da US03).
 */
@Named("notificar-critica")
public class NotificarCriticaHandler implements RequestHandler<DynamodbEvent, Void> {

    private static final Logger LOG = Logger.getLogger(NotificarCriticaHandler.class);

    @Inject
    SnsClient sns;

    // Variáveis de ambiente TOPICO_ALERTAS_ARN e LIMITE_NOTA_CRITICA (template.yaml)
    @ConfigProperty(name = "topico.alertas.arn")
    String topicoArn;

    @ConfigProperty(name = "limite.nota.critica", defaultValue = "4")
    double limite;

    @Override
    public Void handleRequest(DynamodbEvent event, Context context) {
        int enviados = 0;
        for (var record : event.getRecords()) {
            var imagem = record.getDynamodb().getNewImage();
            if (imagem == null || !imagem.containsKey("nota")) {
                continue;
            }
            double nota = Double.parseDouble(imagem.get("nota").getN());
            if (!ClassificadorUrgencia.ehCritica(nota, limite)) {
                continue;
            }
            var descricao = imagem.containsKey("descricao") ? imagem.get("descricao").getS() : "";
            var dataEnvio = imagem.containsKey("dataEnvio") ? imagem.get("dataEnvio").getS() : "";

            // Se falhar, a exceção sobe e o Lambda reprocessa o lote (MaximumRetryAttempts no template)
            sns.publish(PublishRequest.builder()
                    .topicArn(topicoArn)
                    .subject("[Feedback] Avaliação crítica")
                    .message(ClassificadorUrgencia.montarMensagem(descricao, nota, dataEnvio))
                    .build());
            enviados++;
        }
        LOG.infof("Registros: %d | alertas enviados: %d", event.getRecords().size(), enviados);
        return null;
    }
}
