package es.udc.ws.app.model.answer;

import java.time.LocalDateTime;
import java.util.Objects;

public class Answer {
    private Long answerId;
    private String employeeEmail;
    private Long eventId;
    private LocalDateTime answerDate;
    private boolean attending;

    public Answer(Long eventId , String employeeEmail, boolean attending, LocalDateTime answerDate) {
        this.eventId = eventId;
        this.employeeEmail = employeeEmail;
        this.attending = attending;
        this.answerDate = (answerDate != null) ? answerDate.withNano(0) : null;
    }

    /*AÑADIDO PARA DTOS------------------------------------*/
    public Answer(Long answerId, String employeeEmail, Long eventId, boolean attending) {
        this.answerId = answerId;
        this.employeeEmail = employeeEmail;
        this.eventId = eventId;
        this.attending = attending;
    }
    /*----------------------------------------------------*/

    public Answer(Long answerId, Long eventId , String employeeEmail, boolean attending, LocalDateTime answerDate) {
        this(eventId , employeeEmail, attending, answerDate);
        this.answerId = answerId;
    }

    public Long getAnswerId() {return answerId;}
    public Long getEventId() {return eventId;}
    public String getEmployeeEmail() {return employeeEmail;}
    public boolean isAttending() {return attending;}
    public LocalDateTime getAnswerDate() {return answerDate;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return attending == answer.attending && answerId.equals(answer.answerId) && employeeEmail.equals(answer.employeeEmail) && eventId.equals(answer.eventId) && answerDate.equals(answer.answerDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerId, employeeEmail, eventId, answerDate, attending);
    }
}
