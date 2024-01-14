package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientAnswerDtoConversor {
    public static ClientAnswerDto toClientAnswerDto(InputStream jsonAnswer) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAnswer);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode answerIdNode = eventObject.get("answerId");
                Long answerId = (answerIdNode != null) ? answerIdNode.longValue() : null;

                Long eventId = eventObject.get("eventId").longValue();
                String employeeEmail = eventObject.get("employeeEmail").textValue().trim();
                boolean attending = eventObject.get("attending").asBoolean();

                return new ClientAnswerDto(answerId, employeeEmail, eventId, attending);

            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }


    public static List<ClientAnswerDto> toClientAnswerDtos(InputStream jsonAnswers) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAnswers);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode answersArray = (ArrayNode) rootNode;
                List<ClientAnswerDto> answerDtos = new ArrayList<>(answersArray.size());
                for (JsonNode answerNode : answersArray) {
                    answerDtos.add(toClientAnswerDto(answerNode));
                }

                return answerDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientAnswerDto toClientAnswerDto(JsonNode answerNode) throws ParsingException {
        if (answerNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode answerObject = (ObjectNode) answerNode;


            JsonNode answerIdNode = answerObject.get("answerId");
            Long answerId = (answerIdNode != null) ? answerIdNode.longValue() : null;


            String employeeEmail = answerObject.get("employeeEmail").textValue().trim();
            Long eventId  = answerObject.get("eventId").longValue();
            boolean attending = answerObject.get("attending").asBoolean();

            return new ClientAnswerDto(answerId, employeeEmail, eventId, attending);
        }
    }
}
