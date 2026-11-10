package br.com.techchallenge.feedback.avaliacoes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * ESQUELETO da Lambda 1 (Dev 1 evolui nas US01/US02/US03).
 * Exposto pelo API Gateway em POST /avaliacao.
 */
@Path("/avaliacao")
public class AvaliacaoResource {

    private static final Logger LOG = Logger.getLogger(AvaliacaoResource.class);

    @Inject
    DynamoDbClient dynamo;

    // Variável de ambiente TABELA_AVALIACOES (definida no template.yaml)
    @ConfigProperty(name = "tabela.avaliacoes")
    String tabela;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criar(AvaliacaoRequest req) {
        var erro = ValidadorAvaliacao.validar(req);
        if (erro.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErroResponse(erro.get())).build();
        }

        var id = UUID.randomUUID().toString();
        var dataEnvio = Instant.now().toString();
        var descricao = req.descricao().trim();
        double nota = req.nota();

        try {
            dynamo.putItem(PutItemRequest.builder()
                    .tableName(tabela)
                    .item(Map.of(
                            "id", AttributeValue.fromS(id),
                            "descricao", AttributeValue.fromS(descricao),
                            "nota", AttributeValue.fromN(Double.toString(nota)),
                            "dataEnvio", AttributeValue.fromS(dataEnvio)))
                    .build());
        } catch (DynamoDbException e) {
            LOG.error("Falha ao gravar avaliação no DynamoDB", e);
            return Response.serverError().entity(new ErroResponse("Falha ao registrar avaliação")).build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(new AvaliacaoResponse(id, descricao, nota, dataEnvio))
                .build();
    }
}
