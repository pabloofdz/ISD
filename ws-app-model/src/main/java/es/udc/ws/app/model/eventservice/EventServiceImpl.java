package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class EventServiceImpl implements EventService {
    private final DataSource dataSource;
    private SqlEventDao eventDao = null;
    private SqlAnswerDao answerDao = null;

    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        answerDao = SqlAnswerDaoFactory.getDao();
    }

    private void validateEvent(Event event) throws InputValidationException {

        PropertyValidator.validateMandatoryString("name", event.getName());
        PropertyValidator.validateMandatoryString("description", event.getDescription());
        if (event.getCelebrationDate().minusHours(24).isBefore(LocalDateTime.now().withNano(0)))    //Si la fecha de celebración - 24h es anterior a la actual, error
            throw new InputValidationException("Celebration date must be greater than actual date");
        if (event.getDuration() <= 0)
            throw new InputValidationException("Duration bust be greater than 0");
    }

    private void validateEmployeeEmail(String employeeEmail) throws InputValidationException {
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(employeeEmail);

        if (!mather.find()) {
            throw new InputValidationException("Invalid employee email.");
        }
    }

    private void validateLocalDateTime(LocalDateTime startdate, LocalDateTime enddate) throws InputValidationException{
        if (startdate.isAfter(enddate))
            throw new InputValidationException("Start date is after last day.");


    }

    @Override
    public Event addEvent(Event event) throws InputValidationException {

        validateEvent(event);
        event.setCreationDate(LocalDateTime.now());
        event.setCanceled(false);
        event.setEmployeesAttending(0);
        event.setEmployeesNotAttending(0);


        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Event createdEvent = eventDao.create(connection, event);

                /* Commit. */
                connection.commit();

                return createdEvent;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Event findEvent(Long eventId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            return eventDao.find(connection, eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Answer answerEvent(Long eventId, String employeeEmail, boolean attending)
            throws InstanceNotFoundException, InputValidationException, AnswerExpiredException,
            AlreadyAnsweredException, AnswerCancelledEventException {

        validateEmployeeEmail(employeeEmail);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                Event event = eventDao.find(connection, eventId);
                if (event.isCanceled()) {
                    throw new AnswerCancelledEventException(eventId);
                }
                if (event.getCelebrationDate().minusHours(24).isBefore(LocalDateTime.now().withNano(0))){    //Si la fecha de celebración - 24h es anterior a la actual, error
                    throw new AnswerExpiredException(eventId);
                }
                for (Answer answer : answerDao.findByEmail(connection, employeeEmail, false)) {
                    if(Objects.equals(answer.getEventId(), eventId)) throw new AlreadyAnsweredException(eventId, employeeEmail);
                }
                if(attending){
                    event.increaseEmployeesAttending();
                }else{
                    event.increaseEmployeesNotAttending();
                }
                eventDao.update(connection, event);
                Answer answer = answerDao.create(connection, new Answer(eventId, employeeEmail, attending, LocalDateTime.now().withNano(0)));

                /* Commit. */
                connection.commit();

                return answer;

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Answer> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws InputValidationException {

        validateEmployeeEmail(employeeEmail);

        try (Connection connection = dataSource.getConnection()) {
            return answerDao.findByEmail(connection, employeeEmail, onlyAffirmative);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelEvent(Long eventId) throws InstanceNotFoundException, AlreadyCanceledException, EventAlreadyCelebratedException{
        try (Connection connection = dataSource.getConnection()) {
            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Event foundEvent = eventDao.find(connection, eventId);

                if (foundEvent.isCanceled())
                    throw new AlreadyCanceledException(eventId);
                if (LocalDateTime.now().withNano(0).isAfter(foundEvent.getCelebrationDate()))
                    throw new EventAlreadyCelebratedException(eventId);

                foundEvent.setCanceled(true);
                eventDao.update(connection, foundEvent);
                /* Commit. */
                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Event> findEvents(LocalDateTime startDay, LocalDateTime endDay, String keyword) throws InputValidationException{
        validateLocalDateTime(startDay, endDay);
        try (Connection connection = dataSource.getConnection()) {
            return eventDao.findEvents(connection, startDay, endDay, keyword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
