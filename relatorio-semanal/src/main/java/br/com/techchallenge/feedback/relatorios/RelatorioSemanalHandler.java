package br.com.techchallenge.feedback.relatorios;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * ESQUELETO da Lambda 3 (Dev 3 evolui nas US07/US08/US09).
 * Disparada toda segunda-feira pelo EventBridge Scheduler; grava um JSON no S3.
 */
@Named("relatorio-semanal")
public class RelatorioSemanalHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Logger LOG = Logger.getLogger(RelatorioSemanalHandler.class);

    @Inject
    DynamoDbClient dynamo;

    @Inject
    S3Client s3;

    @Inject
    ObjectMapper mapper;

    @ConfigProperty(name = "tabela.avaliacoes")
    String tabela;

    @ConfigProperty(name = "bucket.relatorios")
    String bucket;

    @ConfigProperty(name = "limite.nota.critica", defaultValue = "4")
    double limite;

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> entrada, Context context) {
        var agora = Instant.now();
        var inicio = agora.minus(Duration.ofDays(7));

        // Scan paginado: adequado ao volume do projeto
        var itens = dynamo.scanPaginator(ScanRequest.builder().tableName(tabela).build())
                .items().stream()
                .filter(i -> i.containsKey("dataEnvio") && i.containsKey("nota"))
                .map(i -> new Avaliacao(
                        i.containsKey("descricao") ? i.get("descricao").s() : "",
                        Double.parseDouble(i.get("nota").n()),
                        i.get("dataEnvio").s()))
                .filter(a -> Instant.parse(a.dataEnvio()).isAfter(inicio))
                .toList();

        var relatorio = Consolidador.consolidar(itens, limite, agora.toString());
        var chave = "relatorios/" + DateTimeFormatter.ISO_LOCAL_DATE.format(agora.atZone(ZoneOffset.UTC)) + ".json";

        try {
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(chave).contentType("application/json").build(),
                    RequestBody.fromString(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(relatorio)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar relatório", e);
        }

        LOG.infof("Relatório gravado em s3://%s/%s (%d avaliações)", bucket, chave, itens.size());
        return Map.of("arquivo", chave, "total", itens.size());
    }
}
