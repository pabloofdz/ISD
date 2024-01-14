package es.udc.ws.app.client.service.exceptions;

public class ClientAlreadyCanceledException extends Exception{
    private Long eventId;

    public ClientAlreadyCanceledException(Long eventId) {
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
