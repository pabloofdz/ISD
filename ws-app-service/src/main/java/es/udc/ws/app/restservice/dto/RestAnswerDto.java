package es.udc.ws.app.restservice.dto;

public class RestAnswerDto {
    private Long answerId;
    private String employeeEmail;
    private Long eventId;
    private boolean attending;

    public RestAnswerDto (Long answerId, String employeeEmail, Long eventId, boolean attending) {
        this.answerId=answerId;
        this.employeeEmail=employeeEmail;
        this.eventId=eventId;
        this.attending=attending;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public Long getEventId() {
        return eventId;
    }

    public boolean isAttending() {
        return attending;
    }

    @Override
    public String toString() {
        return "AnswerDto [answerId=" + answerId + ", employeeEmail=" + employeeEmail
                + ", eventId=" + eventId
                + ", attending=" + attending + "]";
    }
}