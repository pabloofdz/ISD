package es.udc.ws.app.model.eventservice.exceptions;

public class AnswerExpiredException extends Exception {
    private Long eventId;

    public AnswerExpiredException(Long eventId) {
        super("Event with id=\"" + eventId + "\n no longer supports answers");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
