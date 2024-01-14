package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEventDao implements SqlEventDao{
    protected AbstractSqlEventDao() {
    }

    @Override
    public Event find(Connection connection, Long eventId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "SELECT name, description, celebrationDate, duration,"
                + " creationDate, canceled, employeesAttending, employeesNotAttending FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventId.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

            /* Get results. */
            i = 1;
            String name = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
            float duration = resultSet.getFloat(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
            boolean canceled = resultSet.getBoolean(i++);
            int employeesAttending = resultSet.getInt(i++);
            int employeesNotAttending = resultSet.getInt(i++);

            /* Return event. */
            return new Event(eventId, name, description, celebrationDate, duration,
                    creationDate, canceled, employeesAttending, employeesNotAttending);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Event event)
            throws InstanceNotFoundException {

        /* Create "queryString".*/
        String queryString = "UPDATE Event"
                + " SET name = ?, description = ?, celebrationDate = ?, duration = ?, "
                + "canceled = ?, employeesAttending = ?, employeesNotAttending = ? WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setFloat(i++, event.getDuration());
            preparedStatement.setBoolean(i++, event.isCanceled());
            preparedStatement.setInt(i++, event.getEmployeesAttending());
            preparedStatement.setInt(i++, event.getEmployeesNotAttending());
            preparedStatement.setLong(i++, event.getEventId());

            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(event.getEventId(),
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long eventId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Event WHERE" + " eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Event> findEvents(Connection connection, LocalDateTime startDay, LocalDateTime endDay, String keyword) {

        /* Create "queryString". */
        String queryString = "SELECT eventId, name, description, celebrationDate,"
                + " duration, creationDate, canceled, employeesAttending, employeesNotAttending FROM Event WHERE";

        if(keyword != null && !keyword.trim().equals(""))
            queryString+=" LOWER(description) LIKE LOWER(?) AND";

        queryString+=" celebrationDate BETWEEN ? AND ? AND celebrationDate > ? ORDER BY celebrationDate";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            if(keyword != null && !keyword.trim().equals(""))
                preparedStatement.setString(i++, "%" + keyword + "%");
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(startDay));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(endDay));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.now().withNano(0)));
            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Event> events = new ArrayList<Event>();

            while (resultSet.next()) {

                i = 1;
                Long eventId = resultSet.getLong(i++);
                String name = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
                float duration = resultSet.getFloat(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                boolean canceled = resultSet.getBoolean(i++);
                int employeesAttending = resultSet.getInt(i++);
                int employeesNotAttending = resultSet.getInt(i++);

                events.add(new Event(eventId, name, description, celebrationDate, duration,
                        creationDate, canceled, employeesAttending, employeesNotAttending));

            }

            return events;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
