package br.com.techchallenge.feedback.notificacoes.dynamodb;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class DynamoDbObjectMapperCustomizer implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.addMixIn(DynamodbEvent.class, DynamoDbMixins.EventoMixin.class);
        mapper.addMixIn(DynamodbEvent.DynamodbStreamRecord.class, DynamoDbMixins.RegistroMixin.class);
        mapper.addMixIn(StreamRecord.class, DynamoDbMixins.ImagemMixin.class);
        mapper.addMixIn(AttributeValue.class, DynamoDbMixins.ValorMixin.class);
    }
}
