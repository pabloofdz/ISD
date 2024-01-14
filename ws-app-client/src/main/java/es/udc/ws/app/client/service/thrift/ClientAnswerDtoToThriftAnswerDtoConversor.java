package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.thrift.ThriftAnswerDto;

import java.util.ArrayList;
import java.util.List;

public class ClientAnswerDtoToThriftAnswerDtoConversor {

    public static List<ClientAnswerDto> toClientAnswerDtos(List<ThriftAnswerDto> answers) {

        List<ClientAnswerDto> clientAnswerDtos = new ArrayList<>(answers.size());

        for (ThriftAnswerDto answer : answers) {
            clientAnswerDtos.add(toClientAnswerDto(answer));
        }
        return clientAnswerDtos;

    }

    public static ClientAnswerDto toClientAnswerDto(ThriftAnswerDto answer) {

        return new ClientAnswerDto(
                answer.getAnswerId(), answer.getEmployeeEmail(), answer.getEventId(), answer.isAttending());

    }
}
