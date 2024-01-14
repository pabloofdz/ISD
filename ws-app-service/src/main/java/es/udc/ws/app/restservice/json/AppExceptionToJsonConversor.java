package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.eventservice.exceptions.*;

public class AppExceptionToJsonConversor {
    public static ObjectNode toAlreadyAnsweredException(AlreadyAnsweredException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyAnswered");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        exceptionObject.put("employeeEmail", (ex.getEmployeeEmail() != null) ? ex.getEmployeeEmail() : null);
        return exceptionObject;
    }

    public static ObjectNode toAlreadyCanceledException(AlreadyCanceledException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyCanceled");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }
    public static ObjectNode toAnswerCancelledEventException(AnswerCancelledEventException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AnswerCancelledEvent");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }
    public static ObjectNode toAnswerExpiredException(AnswerExpiredException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AnswerExpired");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }

    public static ObjectNode toEventAlreadyCelebratedException(EventAlreadyCelebratedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventAlreadyCelebrated");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }

}
