package br.com.techchallenge.feedback.notificacoes.dynamodb;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Mixins que ligam os nomes em PascalCase do evento DynamoDB Stream
 * (Records, NewImage, S, N) aos getters/setters dos tipos do aws-lambda-java-events.
 */
public final class DynamoDbMixins {

    private DynamoDbMixins() {
    }

    public abstract static class EventoMixin {

        @JsonProperty("Records")
        public abstract List<DynamodbEvent.DynamodbStreamRecord> getRecords();

        @JsonProperty("Records")
        public abstract void setRecords(List<DynamodbEvent.DynamodbStreamRecord> records);
    }

    public abstract static class RegistroMixin {

        @JsonProperty("eventID")
        public abstract String getEventID();

        @JsonProperty("eventID")
        public abstract void setEventID(String eventID);

        @JsonProperty("eventName")
        public abstract String getEventName();

        @JsonProperty("eventName")
        public abstract void setEventName(String eventName);
    }

    public abstract static class ImagemMixin {

        @JsonProperty("Keys")
        public abstract Map<String, ?> getKeys();

        @JsonProperty("Keys")
        public abstract void setKeys(Map<String, ?> keys);

        @JsonProperty("NewImage")
        public abstract Map<String, ?> getNewImage();

        @JsonProperty("NewImage")
        public abstract void setNewImage(Map<String, ?> newImage);

        @JsonProperty("OldImage")
        public abstract Map<String, ?> getOldImage();

        @JsonProperty("OldImage")
        public abstract void setOldImage(Map<String, ?> oldImage);

        @JsonProperty("SequenceNumber")
        public abstract String getSequenceNumber();

        @JsonProperty("SequenceNumber")
        public abstract void setSequenceNumber(String sequenceNumber);

        @JsonProperty("SizeBytes")
        public abstract Long getSizeBytes();

        @JsonProperty("SizeBytes")
        public abstract void setSizeBytes(Long sizeBytes);

        @JsonProperty("StreamViewType")
        public abstract String getStreamViewType();

        @JsonProperty("StreamViewType")
        public abstract void setStreamViewType(String streamViewType);

        @JsonProperty("ApproximateCreationDateTime")
        public abstract Date getApproximateCreationDateTime();

        @JsonProperty("ApproximateCreationDateTime")
        public abstract void setApproximateCreationDateTime(Date approximateCreationDateTime);
    }

    public abstract static class ValorMixin {

        @JsonProperty("S")
        public abstract String getS();

        @JsonProperty("S")
        public abstract void setS(String valor);

        @JsonProperty("N")
        public abstract String getN();

        @JsonProperty("N")
        public abstract void setN(String valor);
    }
}
