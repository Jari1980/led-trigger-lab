package se.ledtriggerlab.services;

import se.ledtriggerlab.model.LedEvent;

public class LedEventProcessor {

    public void process(LedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        if (event.getLedId() < 1 || event.getLedId() > 3) {
            throw new IllegalArgumentException(
                    "Invalid LED ID: " + event.getLedId()
            );
        }

        System.out.println(
                "Processing LED event: "
                        + event.getEventId()
                        + ", LED: "
                        + event.getLedId()
        );
    }
}
