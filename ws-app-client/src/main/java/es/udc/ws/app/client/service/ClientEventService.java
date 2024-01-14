package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientEventService {
   public Long addEvent(ClientEventDto event) throws InputValidationException;
   public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException;
   public Long answerEvent(Long eventId, String employeeEmail, boolean attending)
           throws InstanceNotFoundException, InputValidationException, ClientAnswerExpiredException,
           ClientAlreadyAnsweredException, ClientAnswerCancelledEventException;
   public List<ClientAnswerDto> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws InputValidationException;
   public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientAlreadyCanceledException, ClientEventAlreadyCelebratedException;
   public List<ClientEventDto> findEvents(LocalDateTime endDay, String keyword) throws InputValidationException;
}
