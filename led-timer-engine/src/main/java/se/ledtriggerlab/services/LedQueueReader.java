package se.ledtriggerlab.services;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.QueueServiceVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;

import se.ledtriggerlab.model.LedEvent;
import se.ledtriggerlab.model.ExperimentResult;
import se.ledtriggerlab.services.LedEventProcessor;
import se.ledtriggerlab.services.ExperimentResultPublisher;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class LedQueueReader {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;
    private final LedEventProcessor processor;
    private final ExperimentResultPublisher resultPublisher;

    private static final AtomicInteger PROCESSING_ORDER =
            new AtomicInteger(0);

    public LedQueueReader() {

        String connectionString =
                System.getenv("QueueConnection");

        String queueName =
                System.getenv("LedQueueName");

        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .serviceVersion(QueueServiceVersion.V2025_11_05)
                .buildClient();

        this.objectMapper = new ObjectMapper()
                .findAndRegisterModules();

        this.processor = new LedEventProcessor();

        this.resultPublisher =
                new ExperimentResultPublisher("TIMER");
    }

    public void processNextMessage(ExecutionContext context) {

        QueueMessageItem message =
                queueClient.receiveMessage();

        if (message == null) {
            context.getLogger().info(
                    "Timer check: Queue is empty"
            );
            return;
        }

        try {
            String messageText = message.getMessageText();

            context.getLogger().info(
                    "Received LED event: " + messageText
            );

            LedEvent event = objectMapper.readValue(
                    messageText,
                    LedEvent.class
            );

            Instant receivedAt = Instant.now();

            long startTime = System.nanoTime();

            processor.process(event);

            long processingDurationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            Instant processedAt = Instant.now();

            ExperimentResult result = new ExperimentResult();

            result.setExperimentId(event.getExperimentId());
            result.setEventId(event.getEventId());
            result.setEngineType("TIMER");
            result.setLedId(event.getLedId());

            result.setPublishedAt(
                    Instant.parse(event.getPublishedAt())
            );
            result.setReceivedAt(receivedAt);
            result.setProcessedAt(processedAt);

            result.setProcessingDurationMs(
                    processingDurationMs
            );

            result.setProcessingOrder(
                    PROCESSING_ORDER.incrementAndGet()
            );

            resultPublisher.publish(result);

            context.getLogger().info(
                    "Experiment result published. Event: "
                            + event.getEventId()
                            + ", Duration: "
                            + processingDurationMs + " ms"
            );

            queueClient.deleteMessage(
                    message.getMessageId(),
                    message.getPopReceipt()
            );

            context.getLogger().info(
                    "Successfully processed and deleted event: "
                            + event.getEventId()
            );

        } catch (Exception e) {

            context.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to process queue message. "
                            + "Message will not be deleted.",
                    e
            );

            throw new RuntimeException(
                    "Failed to process LED event", e
            );
        }
    }
}
