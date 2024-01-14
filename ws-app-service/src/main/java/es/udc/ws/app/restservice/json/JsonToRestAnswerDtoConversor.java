package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestAnswerDto;
import es.udc.ws.app.restservice.dto.RestEventDto;

import java.util.List;

public class JsonToRestAnswerDtoConversor {
    public static ObjectNode toObjectNode(RestAnswerDto answer) {

        ObjectNode answerNode = JsonNodeFactory.instance.objectNode();

        if (answer.getAnswerId() != null) {
            answerNode.put("answerId", answer.getAnswerId());
        }
        answerNode.put("employeeEmail", answer.getEmployeeEmail()).
                put("eventId", answer.getEventId()).
                put("attending", answer.isAttending());

        return answerNode;
    }

    public static ArrayNode toArrayNode(List<RestAnswerDto> answers) {

        ArrayNode answerNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < answers.size(); i++) {
            RestAnswerDto answerDto = answers.get(i);
            ObjectNode answerObject = toObjectNode(answerDto);
            answerNode.add(answerObject);
        }

        return answerNode;
    }


}
