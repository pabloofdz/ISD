package es.udc.ws.app.client.service.exceptions;

public class ClientAnswerExpiredException extends Exception {
    private Long eventId;

    public ClientAnswerExpiredException(Long eventId) {
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
