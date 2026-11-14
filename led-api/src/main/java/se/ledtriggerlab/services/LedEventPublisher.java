package se.ledtriggerlab.services;

import com.azure.storage.queue.QueueClient;

public class LedEventPublisher {

    private final QueueClient ledQueueClient;
    private final QueueClient timerQueueClient;

    public LedEventPublisher(
            QueueClient ledQueueClient,
            QueueClient timerQueueClient) {

        this.ledQueueClient = ledQueueClient;
        this.timerQueueClient = timerQueueClient;
    }

    public void publish(String eventMessage) {

        ledQueueClient.sendMessage(eventMessage);

        timerQueueClient.sendMessage(eventMessage);
    }
}
