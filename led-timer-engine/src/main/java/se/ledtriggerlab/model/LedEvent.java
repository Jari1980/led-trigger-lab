package se.ledtriggerlab.model;

public class LedEvent {

    private String eventId;
    private int ledId;

    public LedEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getLedId() {
        return ledId;
    }

    public void setLedId(int ledId) {
        this.ledId = ledId;
    }
}
