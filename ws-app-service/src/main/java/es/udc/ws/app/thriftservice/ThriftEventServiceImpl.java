package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;


/*MIRAR EXCEPCIONES SI BIEN + SI ESTÁ BIEN EL PARSE DE STRING A LOCALDATETIME EN FINDEVENTS*/
public class ThriftEventServiceImpl implements  ThriftEventService.Iface{

    @Override
    public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException {

        Event event = EventToThriftEventDtoConversor.toEvent(eventDto);

        try {
            Event addedEvent = EventServiceFactory.getService().addEvent(event);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }

    @Override
    public List<ThriftEventDto> findEvents(String endDateString, String keyword) throws ThriftInputValidationException {

        LocalDateTime actualDate=LocalDateTime.now();
        LocalDateTime endDate=LocalDateTime.parse(endDateString);
        try{
            List<Event> events = EventServiceFactory.getService().findEvents(actualDate, endDate, keyword);
            return EventToThriftEventDtoConversor.toThriftEventDtos(events);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }

    @Override
    public ThriftEventDto findEvent(long eventId) throws ThriftInstanceNotFoundException {
        try {

            Event event = EventServiceFactory.getService().findEvent(eventId);
            return EventToThriftEventDtoConversor.toThriftEventDto(event);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public ThriftAnswerDto answerEvent(long eventId, String employeeEmail, boolean attending) throws ThriftInputValidationException,
            ThriftInstanceNotFoundException, ThriftAnswerExpiredException, ThriftAlreadyAnsweredException, ThriftAnswerCancelledEventException{

        try {
            Answer answer = EventServiceFactory.getService().answerEvent(eventId, employeeEmail, attending);
            return AnswerToThriftAnswerDtoConversor.toThriftAnswerDto(answer);

        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (AnswerExpiredException e) {
            throw new ThriftAnswerExpiredException(e.getEventId());
        } catch (AlreadyAnsweredException e) {
            throw new ThriftAlreadyAnsweredException(e.getEventId(), e.getEmployeeEmail());
        } catch (AnswerCancelledEventException e) {
            throw new ThriftAnswerCancelledEventException(e.getEventId());
        }

    }

    @Override
    public void cancelEvent(long eventId) throws ThriftInstanceNotFoundException,
            ThriftAlreadyCanceledException, ThriftEventAlreadyCelebratedException{
        try {
            EventServiceFactory.getService().cancelEvent(eventId);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (AlreadyCanceledException e) {
            throw new ThriftAlreadyCanceledException(e.getEventId());
        } catch (EventAlreadyCelebratedException e) {
            throw new ThriftEventAlreadyCelebratedException(e.getEventId());
        }

    }

    @Override
    public List<ThriftAnswerDto> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws ThriftInputValidationException {
        try{
            List<Answer> answers = EventServiceFactory.getService().findEmployeeAnswers(employeeEmail, onlyAffirmative);
            return AnswerToThriftAnswerDtoConversor.toThriftAnswerDtos(answers);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }
}
