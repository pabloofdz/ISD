package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.thrift.ThriftEventDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventToThriftEventDtoConversor {
    public static Event toEvent(ThriftEventDto event) {
        return new Event(event.getEventId(), event.getName(), event.getDescription(), LocalDateTime.parse(event.getCelebrationDate()), Double.valueOf(event.getDuration()).floatValue(), event.isCanceled(), event.getEmployeesAttending(), event.getTotalAnswers()-event.getEmployeesAttending());
    }

    public static List<ThriftEventDto> toThriftEventDtos(List<Event> events) {

        List<ThriftEventDto> dtos = new ArrayList<>(events.size());

        for (Event event : events) {
            dtos.add(toThriftEventDto(event));
        }
        return dtos;

    }

    public static ThriftEventDto toThriftEventDto(Event event) {

        return new ThriftEventDto(event.getEventId(), event.getName(), event.getDescription(), event.getCelebrationDate().toString(), event.getDuration(), event.isCanceled(), event.getEmployeesAttending(), event.getEmployeesAttending()+event.getEmployeesNotAttending());

    }
}
