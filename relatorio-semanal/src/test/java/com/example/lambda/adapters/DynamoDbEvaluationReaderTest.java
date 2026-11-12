package com.example.lambda.adapters;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DynamoDbEvaluationReaderTest {
    @Test
    void readsProducerSchemaAndDecimalScoresAcrossPagesWithExclusiveEnd() {
        var start = Instant.parse("2026-11-01T03:00:00Z");
        var end = Instant.parse("2026-11-08T03:00:00Z");
        DynamoDbClient client = (DynamoDbClient) Proxy.newProxyInstance(
                DynamoDbClient.class.getClassLoader(), new Class<?>[]{DynamoDbClient.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "scanPaginator" -> new ScanIterable((DynamoDbClient) proxy,
                            (software.amazon.awssdk.services.dynamodb.model.ScanRequest) args[0]);
                    case "scan" -> {
                        var request = (software.amazon.awssdk.services.dynamodb.model.ScanRequest) args[0];
                        yield request.exclusiveStartKey().isEmpty()
                                ? ScanResponse.builder().items(item(start, "9.0"), item(start.minusSeconds(1), "2.0"))
                                    .lastEvaluatedKey(Map.of("id", AttributeValue.fromS("next"))).build()
                                : ScanResponse.builder().items(item(end.minusSeconds(1), "4.5"), item(end, "7.0")).build();
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });

        var evaluations = new DynamoDbEvaluationReader(client, "avaliacoes-dev")
                .getEvaluationsToReport(start, end);

        assertEquals(2, evaluations.size());
        assertEquals(9.0, evaluations.get(0).getEvaluation());
        assertEquals(4.5, evaluations.get(1).getEvaluation());
        assertEquals(start, evaluations.get(0).getCreatedDate());
    }

    private Map<String, AttributeValue> item(Instant date, String score) {
        return Map.of("id", AttributeValue.fromS(UUID.randomUUID().toString()),
                "descricao", AttributeValue.fromS("Feedback"),
                "dataEnvio", AttributeValue.fromS(date.toString()),
                "nota", AttributeValue.fromN(score));
    }
}
