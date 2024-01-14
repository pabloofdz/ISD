package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventToRestEventDtoConversor {

    public static List<RestEventDto> toRestEventDtos(List<Event> events) {
        List<RestEventDto> eventDtos = new ArrayList<>(events.size());
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            eventDtos.add(toRestEventDto(event));
        }
        return eventDtos;
    }

    public static RestEventDto toRestEventDto(Event event) {
        return new RestEventDto(event.getEventId(), event.getName(), event.getDescription(), event.getCelebrationDate(),
                event.getDuration(), event.isCanceled(), event.getEmployeesAttending(), event.getEmployeesAttending()+event.getEmployeesNotAttending());
    }

    public static Event toEvent(RestEventDto event) {
        return new Event(event.getEventId(), event.getName(), event.getDescription(), event.getCelebrationDate(),
                event.getDuration(), event.isCanceled(), event.getEmployeesAttending(), event.getTotalAnswers()-event.getEmployeesAttending());
    }

}