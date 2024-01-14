package es.udc.ws.app.model.event;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlEventDao {
    public Event create(Connection connection, Event event);

    //Necesario para cambiar estado
    public void update(Connection connection, Event event)
            throws InstanceNotFoundException;

    public Event find(Connection connection, Long eventId)
            throws InstanceNotFoundException;
    public List<Event> findEvents(Connection connection, LocalDateTime startDay, LocalDateTime endDay, String keyword);

    // Necesario para las pruebas
    public void remove(Connection connection, Long eventId)
            throws InstanceNotFoundException;

}