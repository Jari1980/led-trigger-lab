package se.ledtriggerlab.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExperimentResultReader {

    private static final String TABLE_NAME = "ExperimentResults";

    private final TableClient tableClient;

    public ExperimentResultReader() {

        String connectionString =
                System.getenv("TableConnection");

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

    public List<Map<String, Object>> getResults(String experimentId) {

        List<Map<String, Object>> results = new ArrayList<>();

        String filter = "PartitionKey eq '" +
                experimentId.replace("'", "''") + "'";

        ListEntitiesOptions options =
                new ListEntitiesOptions().setFilter(filter);

        for (TableEntity entity :
                tableClient.listEntities(options, null, null)) {

            Map<String, Object> result = Map.of(
                    "experimentId", entity.getPartitionKey(),
                    "eventId", entity.getProperty("eventId"),
                    "engineType", entity.getProperty("engineType"),
                    "ledId", entity.getProperty("ledId"),
                    "publishedAt", entity.getProperty("publishedAt"),
                    "receivedAt", entity.getProperty("receivedAt"),
                    "processedAt", entity.getProperty("processedAt"),
                    "processingDurationMs",
                    entity.getProperty("processingDurationMs"),
                    "processingOrder",
                    entity.getProperty("processingOrder")
            );

            results.add(result);
        }

        return results;
    }
}
