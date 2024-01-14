package es.udc.ws.app.model.eventservice.exceptions;

public class AlreadyCanceledException extends Exception{
    private Long eventId;

    public AlreadyCanceledException(Long eventId) {
        super("Event with id=\"" + eventId + "\n cannot be canceled because it has been canceled");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
