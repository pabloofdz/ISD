package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;
import es.udc.ws.app.client.service.exceptions.*;

import java.io.InputStream;

public class JsonToClientExceptionConversor {
    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("AlreadyCanceled")) {
                    return toAlreadyCanceledException(rootNode);
                }else if (errorType.equals("AlreadyAnswered")){
                    return toAlreadyAnsweredException(rootNode);
                }else if (errorType.equals("AnswerCancelledEvent")){
                    return toAnswerCancelledEventException(rootNode);
                }else if (errorType.equals("AnswerExpired")){
                    return toAnswerExpiredException(rootNode);
                }else if (errorType.equals("EventAlreadyCelebrated")){
                    return toEventAlreadyCelebratedException(rootNode);
                }else{
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    private static ClientAlreadyCanceledException toAlreadyCanceledException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientAlreadyCanceledException(eventId);
    }

    private static ClientAlreadyAnsweredException toAlreadyAnsweredException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        String employeeEmail = rootNode.get("employeeEmail").textValue();
        return new ClientAlreadyAnsweredException(eventId, employeeEmail);
    }

    private static ClientAnswerCancelledEventException toAnswerCancelledEventException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientAnswerCancelledEventException(eventId);
    }

    private static ClientAnswerExpiredException toAnswerExpiredException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientAnswerExpiredException(eventId);
    }

    private static ClientEventAlreadyCelebratedException toEventAlreadyCelebratedException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientEventAlreadyCelebratedException(eventId);
    }

}

