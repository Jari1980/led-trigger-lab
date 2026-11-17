package se.ledtriggerlab.model;


public class LedEvent {

    private String eventId;
    private int ledId;
    private String experimentId;
    private String publishedAt;

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

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
