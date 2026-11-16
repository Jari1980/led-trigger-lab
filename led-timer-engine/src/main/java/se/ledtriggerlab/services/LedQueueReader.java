package se.ledtriggerlab.services;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.QueueServiceVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;

import se.ledtriggerlab.model.LedEvent;

public class LedQueueReader {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;
    private final LedEventProcessor processor;

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

        this.objectMapper = new ObjectMapper();
        this.processor = new LedEventProcessor();
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

            processor.process(event);

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
