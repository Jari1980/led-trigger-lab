package se.ledtriggerlab.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.TableEntity;

import se.ledtriggerlab.model.ExperimentResult;

import java.time.Instant;

public class ExperimentResultPublisher {

    private static final String TABLE_NAME = "ExperimentResults";

    private final TableClient tableClient;
    private final String engineType;

    public ExperimentResultPublisher(String engineType) {

        this.engineType = engineType;

        String connectionString =
                System.getenv("TableConnection");

        System.out.println(
                "TableConnection available: "
                        + (connectionString != null)
        );

        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException(
                    "TableConnection is missing"
            );
        }

        TableServiceClient serviceClient =
                new TableServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();

        serviceClient.createTableIfNotExists(TABLE_NAME);

        this.tableClient =
                serviceClient.getTableClient(TABLE_NAME);
    }

    public void publish(ExperimentResult result) {

        if (result == null) {
            throw new IllegalArgumentException(
                    "ExperimentResult cannot be null"
            );
        }

        if (result.getExperimentId() == null
                || result.getEventId() == null) {
            throw new IllegalArgumentException(
                    "experimentId and eventId are required"
            );
        }

        String rowKey =
                engineType + "_" + result.getEventId();

        TableEntity entity = new TableEntity(
                result.getExperimentId(),
                rowKey
        );

        entity.addProperty("eventId", result.getEventId());
        entity.addProperty("engineType", engineType);
        entity.addProperty("ledId", result.getLedId());

        entity.addProperty(
                "publishedAt",
                toString(result.getPublishedAt())
        );

        entity.addProperty(
                "receivedAt",
                toString(result.getReceivedAt())
        );

        entity.addProperty(
                "processedAt",
                toString(result.getProcessedAt())
        );

        entity.addProperty(
                "processingDurationMs",
                result.getProcessingDurationMs()
        );

        entity.addProperty(
                "processingOrder",
                result.getProcessingOrder()
        );

        tableClient.createEntity(entity);
    }

    private String toString(Instant instant) {
        return instant == null ? null : instant.toString();
    }
}
