package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyCanceledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsServlet extends RestHttpServletTemplate {
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String path=req.getPathInfo();
        if (path == null || path.equals("/")) {
            String endDateString=ServletUtils.getMandatoryParameter(req, "endDate");
            LocalDateTime endDate;
            try {
                endDate = LocalDateTime.parse(endDateString); //getMandatoryParameter porque es obligatorio
            }catch (DateTimeParseException e){
                throw new InputValidationException("Invalid endDate");
            }
            String keyword = req.getParameter("keyword");
            LocalDateTime actualDate = LocalDateTime.now().withNano(0);
            List<Event> events = EventServiceFactory.getService().findEvents(actualDate, endDate, keyword);

            List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
        }else{
            Long eventId = ServletUtils.getIdFromPath(req, "event");
            Event event = EventServiceFactory.getService().findEvent(eventId);
            RestEventDto eventDto= EventToRestEventDtoConversor.toRestEventDto(event);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
        }
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String path=req.getPathInfo();
        if (path == null || path.equals("/")) {
            RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
            Event event = EventToRestEventDtoConversor.toEvent(eventDto);

            event = EventServiceFactory.getService().addEvent(event);

            eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            String eventURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventId();
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", eventURL);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }else{
            Long eventId = ServletUtils.getIdFromPath(req, "event");
            try {
                EventServiceFactory.getService().cancelEvent(eventId);
            }catch(AlreadyCanceledException e){
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAlreadyCanceledException(e), null);
                return;
            }catch(EventAlreadyCelebratedException e){
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toEventAlreadyCelebratedException(e), null);
                return;
            }
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
        }
    }
}
