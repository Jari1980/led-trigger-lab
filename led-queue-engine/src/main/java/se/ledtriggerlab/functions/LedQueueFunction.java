package se.ledtriggerlab.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

import se.ledtriggerlab.model.LedEvent;
import se.ledtriggerlab.services.LedEventProcessor;

public class LedQueueFunction {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private final LedEventProcessor processor =
            new LedEventProcessor();

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

            processor.process(event);

            context.getLogger().info(
                    "Successfully processed LED event: "
                            + event.getEventId()
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
