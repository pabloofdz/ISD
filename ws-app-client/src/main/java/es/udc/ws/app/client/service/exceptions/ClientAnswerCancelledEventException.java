package es.udc.ws.app.client.service.exceptions;

public class ClientAnswerCancelledEventException extends Exception {
    private Long eventId;

    public ClientAnswerCancelledEventException(Long eventId) {
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
