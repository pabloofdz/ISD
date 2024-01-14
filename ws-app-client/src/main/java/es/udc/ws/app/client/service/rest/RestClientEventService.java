package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import es.udc.ws.app.client.service.rest.json.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

public class RestClientEventService implements ClientEventService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";

    private String endpointAddress;

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException{
        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "events").
                    bodyStream(toInputStream(event), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDateTime endDay, String keyword) throws InputValidationException{
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "events?endDate=" +
                            endDay + "&keyword=" + URLEncoder.encode(keyword, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity()
                    .getContent());

        } catch (InputValidationException e){
            throw e;
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException{
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "events/"
                            + eventId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity()
                    .getContent());

        } catch (InstanceNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Long answerEvent(Long eventId, String employeeEmail, boolean attending)
            throws InstanceNotFoundException, InputValidationException, ClientAnswerExpiredException,
            ClientAlreadyAnsweredException, ClientAnswerCancelledEventException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "answers").
                    bodyForm(
                            Form.form().
                                    add("employeeEmail", employeeEmail).
                                    add("eventId", Long.toString(eventId)).
                                    add("attending", Boolean.toString(attending)).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientAnswerDtoConversor.toClientAnswerDto(
                    response.getEntity().getContent()).getAnswerId();

        } catch (InputValidationException | InstanceNotFoundException  | ClientAnswerExpiredException  | ClientAlreadyAnsweredException  | ClientAnswerCancelledEventException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientAlreadyCanceledException, ClientEventAlreadyCelebratedException {
        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "events/" + eventId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InstanceNotFoundException | ClientAlreadyCanceledException | ClientEventAlreadyCelebratedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<ClientAnswerDto> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws InputValidationException {
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "answers?employeeEmail="
                            + URLEncoder.encode(employeeEmail, "UTF-8") + "&onlyAffirmative=" + onlyAffirmative).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientAnswerDtoConversor.toClientAnswerDtos(response.getEntity()
                    .getContent());

        } catch (InputValidationException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {

        try {

            int statusCode = response.getStatusLine().getStatusCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {

                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());
                default:
                    throw new RuntimeException("HTTP error; status code = "
                            + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private InputStream toInputStream(ClientEventDto event) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
