package se.ledtriggerlab.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

import se.ledtriggerlab.model.LedEvent;
import se.ledtriggerlab.model.ExperimentResult;
import se.ledtriggerlab.services.LedEventProcessor;
import se.ledtriggerlab.services.ExperimentResultPublisher;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class LedQueueFunction {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private static final AtomicInteger PROCESSING_ORDER =
            new AtomicInteger(0);

    private final LedEventProcessor processor =
            new LedEventProcessor();

    private final ExperimentResultPublisher resultPublisher =
            new ExperimentResultPublisher("QUEUE");

    @FunctionName("LedQueueFunction")
    public void processQueueMessage(
            @QueueTrigger(
                    name = "message",
                    queueName = "led-queue-experiment",
                    connection = "AzureWebJobsStorage"
            ) String message,
            final ExecutionContext context) {

        try {
            context.getLogger().info(
                    "Received LED event: " + message
            );

            LedEvent event =
                    OBJECT_MAPPER.readValue(message, LedEvent.class);

            Instant receivedAt = Instant.now();
            long startTime = System.nanoTime();

            processor.process(event);

            long endTime = System.nanoTime();
            Instant processedAt = Instant.now();

            long processingDurationMs =
                    (endTime - startTime) / 1_000_000;

            ExperimentResult result = new ExperimentResult();

            result.setExperimentId(event.getExperimentId());
            result.setEventId(event.getEventId());
            result.setEngineType("QUEUE");
            result.setLedId(event.getLedId());

            result.setPublishedAt(
                    Instant.parse(event.getPublishedAt())
            );

            result.setReceivedAt(receivedAt);
            result.setProcessedAt(processedAt);

            result.setProcessingDurationMs(processingDurationMs);

            result.setProcessingOrder(
                    PROCESSING_ORDER.incrementAndGet()
            );

            resultPublisher.publish(result);

            context.getLogger().info(
                    "Successfully processed and reported event: "
                            + event.getEventId()
                            + ", processing time: "
                            + processingDurationMs
                            + " ms"
            );

        } catch (Exception e) {

            context.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to process LED event: " + message,
                    e
            );

            throw new RuntimeException(
                    "Failed to process LED event", e
            );
        }
    }
}
