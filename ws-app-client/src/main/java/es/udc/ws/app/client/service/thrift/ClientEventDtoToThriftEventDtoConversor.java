package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.thrift.ThriftEventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientEventDtoToThriftEventDtoConversor {
    public static ThriftEventDto toThriftEventDto(
            ClientEventDto clientEventDto) {

        Long eventId = clientEventDto.getEventId();

        float duration = Duration.between(clientEventDto.getCelebrationDate(), clientEventDto.getEndDate()).toSeconds()/3600f;

        return new ThriftEventDto(
                eventId == null ? -1 : eventId.longValue(),
                clientEventDto.getName(), clientEventDto.getDescription(), clientEventDto.getCelebrationDate().toString(), duration, clientEventDto.isCanceled(), clientEventDto.getEmployeesAttending(), clientEventDto.getTotalAnswers());

    }

    public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> events) {

        List<ClientEventDto> clientEventDtos = new ArrayList<>(events.size());

        for (ThriftEventDto event : events) {
            clientEventDtos.add(toClientEventDto(event));
        }
        return clientEventDtos;

    }

    public static ClientEventDto toClientEventDto(ThriftEventDto event) {
        LocalDateTime celebrationDate = LocalDateTime.parse(event.getCelebrationDate());
        LocalDateTime endDate = celebrationDate.plusSeconds((long)event.getDuration()*3600);
        return new ClientEventDto(
                event.getEventId(), event.getName(), event.getDescription(), celebrationDate, endDate, event.isCanceled(), event.getEmployeesAttending(), event.getTotalAnswers());

    }
}
