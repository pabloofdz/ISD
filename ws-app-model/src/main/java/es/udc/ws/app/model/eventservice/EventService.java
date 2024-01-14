package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    public Event addEvent(Event event) throws InputValidationException;
    public Event findEvent(Long eventId) throws InstanceNotFoundException;
    public Answer answerEvent(Long eventId, String employeeEmail, boolean attending)
            throws InstanceNotFoundException, InputValidationException, AnswerExpiredException,
            AlreadyAnsweredException, AnswerCancelledEventException;
    public List<Answer> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws InputValidationException;
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, AlreadyCanceledException, EventAlreadyCelebratedException;
    public List<Event> findEvents(LocalDateTime startDay, LocalDateTime endDay, String keyword) throws InputValidationException;
}
