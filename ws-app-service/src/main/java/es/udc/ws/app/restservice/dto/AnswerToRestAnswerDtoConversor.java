package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.answer.Answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerToRestAnswerDtoConversor {

    public static List<RestAnswerDto> toRestAnswerDtos(List<Answer> answers) {
        List<RestAnswerDto> answerDtos = new ArrayList<>(answers.size());
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            answerDtos.add(toRestAnswerDto(answer));
        }
        return answerDtos;
    }

    public static RestAnswerDto toRestAnswerDto(Answer answer) {
        return new RestAnswerDto(answer.getAnswerId(), answer.getEmployeeEmail(), answer.getEventId(), answer.isAttending());
    }

    public static Answer toAnswer(RestAnswerDto answer) {
        return new Answer(answer.getAnswerId(), answer.getEmployeeEmail(), answer.getEventId(), answer.isAttending());
    }

}