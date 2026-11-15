package se.ledtriggerlab.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

public class LedQueueFunction {

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

        } catch (Exception e) {

            context.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "FAILED TO PROCESS LED EVENT: " + message,
                    e
            );

            throw new RuntimeException(
                    "Failed to process LED event", e
            );
        }
    }
}
