package se.ledtriggerlab.config;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueServiceVersion;

public class QueueConfiguration {

    private final QueueClient ledQueueClient;
    private final QueueClient timerQueueClient;

    public QueueConfiguration() {

        String connectionString =
                System.getenv("QueueConnection");

        String ledQueueName =
                System.getenv("LedQueueName");

        String timerQueueName =
                System.getenv("TimerQueueName");

        this.ledQueueClient = createClient(
                connectionString,
                ledQueueName
        );

        this.timerQueueClient = createClient(
                connectionString,
                timerQueueName
        );
    }

    private QueueClient createClient(
            String connectionString,
            String queueName) {

        return new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .serviceVersion(QueueServiceVersion.V2026_06_06)
                .buildClient();
    }

    public QueueClient getLedQueueClient() {
        return ledQueueClient;
    }

    public QueueClient getTimerQueueClient() {
        return timerQueueClient;
    }

    public void verifyQueues() {
        ledQueueClient.createIfNotExists();
        timerQueueClient.createIfNotExists();
    }
}
