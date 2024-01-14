package es.udc.ws.app.model.eventservice.exceptions;

public class AnswerCancelledEventException extends Exception {
    private Long eventId;

    public AnswerCancelledEventException(Long eventId) {
        super("Event with id=\"" + eventId + "\n cannot be answered because it has been cancelled");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
