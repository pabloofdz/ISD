package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.AnswerCancelledEventException;
import es.udc.ws.app.model.eventservice.exceptions.AnswerExpiredException;
import es.udc.ws.app.restservice.dto.AnswerToRestAnswerDtoConversor;
import es.udc.ws.app.restservice.dto.RestAnswerDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestAnswerDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnswersServlet extends RestHttpServletTemplate{
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException{
        ServletUtils.checkEmptyPath(req);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(req,"eventId");
        String employeeEmail = ServletUtils.getMandatoryParameter(req,"employeeEmail");
        String attendingString=ServletUtils.getMandatoryParameter(req,"attending");
        if (!(attendingString.equals("true") || attendingString.equals("false")))
            throw new InputValidationException("attending parameter must be true or false");
        boolean attending = Boolean.parseBoolean(attendingString);

        Answer answer;
        try {
            answer = EventServiceFactory.getService().answerEvent(eventId, employeeEmail, attending);

        } catch (AnswerExpiredException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAnswerExpiredException(e),
                    null);
            return;
        } catch (AlreadyAnsweredException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAlreadyAnsweredException(e),
                    null);
            return;
        } catch (AnswerCancelledEventException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAnswerCancelledEventException(e),
                    null);
            return;
        }

        RestAnswerDto answerDto = AnswerToRestAnswerDtoConversor.toRestAnswerDto(answer);
        String answerURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + answer.getAnswerId().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", answerURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestAnswerDtoConversor.toObjectNode(answerDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {
        String employeeEmail = ServletUtils.getMandatoryParameter(req,"employeeEmail");
        String onlyAffirmativeString=ServletUtils.getMandatoryParameter(req,"onlyAffirmative");
        if (!(onlyAffirmativeString.equals("true") || onlyAffirmativeString.equals("false")))
            throw new InputValidationException("onlyAffirmative parameter must be true or false");
        boolean onlyAffirmative = Boolean.parseBoolean(onlyAffirmativeString);
        List<Answer> answerList;
        answerList = EventServiceFactory.getService().findEmployeeAnswers(employeeEmail, onlyAffirmative);
        List<RestAnswerDto> answersDto = AnswerToRestAnswerDtoConversor.toRestAnswerDtos(answerList);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestAnswerDtoConversor.toArrayNode(answersDto), null);
    }
    
}
