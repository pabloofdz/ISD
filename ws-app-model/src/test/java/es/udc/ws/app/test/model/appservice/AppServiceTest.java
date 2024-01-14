package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import javax.sql.DataSource;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppServiceTest {
    private final long NON_EXISTENT_EVENT_ID = -1;

    private final String VALID_EMPLOYEE_EMAIL = "user@udc.es";
    private final String INVALID_EMPLOYEE_EMAIL = "";

    private final boolean ATTENDING = true;
    private final boolean NOT_ATTENDING = false;

    private static EventService eventService = null;

    private static SqlEventDao eventDao = null;

    private static SqlAnswerDao answerDao = null;

    @BeforeAll
    public static void init() {

        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();
        eventDao = SqlEventDaoFactory.getDao();
        answerDao = SqlAnswerDaoFactory.getDao();

    }

    private Event getValidEvent(String name) {
        LocalDateTime celebrationDate = LocalDateTime.of(2023, 12, 25, 12, 0).withNano(0);
        return new Event(name, "Event description", celebrationDate, 19.95F);
    }

    private Event getValidEvent() { return getValidEvent("Event title"); }

    private Event createEvent(Event event) {

        Event addedEvent = null;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;

    }

    private Answer createAnswer(String employeeEmail, Long eventId, boolean attending) {

        Answer addedAnswer = null;
        try {
            addedAnswer = eventService.answerEvent(eventId, employeeEmail, attending);
        } catch (InputValidationException | InstanceNotFoundException | AnswerExpiredException | AlreadyAnsweredException | AnswerCancelledEventException e) {
            throw new RuntimeException(e);
        }
        return addedAnswer;

    }

    private void updateEvent(Event event) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                eventDao.update(connection, event);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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

    private void removeEvent(Long eventId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                eventDao.remove(connection, eventId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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

    private void removeAnswer(Long answerId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                answerDao.remove(connection, answerId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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

    public Answer findAnswer(Long answerId) throws InstanceNotFoundException {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {
            return answerDao.find(connection, answerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    public void testFindNonExistentEvent() {
        assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTENT_EVENT_ID));
    }
    @Test
    public void testAddEventAndFindEvent() throws InputValidationException, InstanceNotFoundException {

        Event event = getValidEvent();
        Event addedEvent = null;

        try {

            // Create Event
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedEvent = eventService.addEvent(event);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            // Find Event
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());

            assertEquals(addedEvent, foundEvent);
            assertEquals(foundEvent.getName(), event.getName());
            assertEquals(foundEvent.getDescription(), event.getDescription());
            assertEquals(foundEvent.getCelebrationDate(), event.getCelebrationDate());
            assertEquals(foundEvent.getDuration(), event.getDuration());
            assertEquals(foundEvent.isCanceled(), event.isCanceled());
            assertEquals(foundEvent.getEmployeesAttending(), event.getEmployeesAttending());
            assertEquals(foundEvent.getEmployeesNotAttending(), event.getEmployeesNotAttending());
            assertTrue((foundEvent.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundEvent.getCreationDate().compareTo(afterCreationDate) <= 0));

        } finally {
            // Clear Database
            if (addedEvent!=null) {
                removeEvent(addedEvent.getEventId());
            }
        }
    }

    @Test
    public void testAddInvalidEvent() {

        // Check event title not null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setName(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Check event title not empty
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setName("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Check event description not null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Check event description not null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Check celebration date greater than actual
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setCelebrationDate(LocalDateTime.now().withNano(0));
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Check event duration > 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration(0F);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Check event duration > 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration(-1F);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAnswerEvent()
            throws InstanceNotFoundException, InputValidationException, AlreadyAnsweredException, AnswerExpiredException, AnswerCancelledEventException {

        Event event = createEvent(getValidEvent());
        Answer answer = null;

        try {

            // Answer event
            answer = eventService.answerEvent(event.getEventId(), VALID_EMPLOYEE_EMAIL, ATTENDING);

            // Find answer
            Answer foundAnswer = findAnswer(answer.getAnswerId());

            // Check answer
            assertEquals(answer, foundAnswer);
            assertEquals(VALID_EMPLOYEE_EMAIL, foundAnswer.getEmployeeEmail());
            assertEquals(answer.isAttending(), foundAnswer.isAttending());
            assertEquals(answer.getAnswerId(), foundAnswer.getAnswerId());
            assertEquals(answer.getAnswerDate(), foundAnswer.getAnswerDate());

            // Find event
            Event foundEvent = eventService.findEvent(event.getEventId());

            // Check event
            assertEquals(1, foundEvent.getEmployeesAttending());
            assertEquals(0, foundEvent.getEmployeesNotAttending());


        } finally {
            // Clear database: remove answer (if created) and event
            if (answer != null) {
                removeAnswer(answer.getAnswerId());
            }
            removeEvent(event.getEventId());
        }

    }

    @Test
    public void testAnswerNonExistentEvent() {

        assertThrows(InstanceNotFoundException.class, () -> {
            Answer answer = eventService.answerEvent(NON_EXISTENT_EVENT_ID, VALID_EMPLOYEE_EMAIL, ATTENDING);
            removeAnswer(answer.getAnswerId());
        });

    }

    @Test
    public void testAnswerEventWithInvalidEmployeeEmail() {

        Event event = createEvent(getValidEvent());
        try {
            assertThrows(InputValidationException.class, () -> {
                Answer answer = eventService.answerEvent(event.getEventId(), INVALID_EMPLOYEE_EMAIL, ATTENDING);
                removeAnswer(answer.getAnswerId());
            });
        } finally {
            // Clear database
            removeEvent(event.getEventId());
        }

    }

    @Test
    public void testAnswerCancelledEvent() {
        Event tmp = getValidEvent();
        Event event = createEvent(tmp);
        event.setCanceled(true);
        updateEvent(event);

        try {
            assertThrows(AnswerCancelledEventException.class, () -> {
                Answer answer = eventService.answerEvent(event.getEventId(), VALID_EMPLOYEE_EMAIL, ATTENDING);
                removeAnswer(answer.getAnswerId());
            });
        } finally {
            // Clear database
            removeEvent(event.getEventId());
        }
    }


    @Test
    public void testReAnswerEvent() throws AlreadyAnsweredException, InstanceNotFoundException, AnswerExpiredException, AnswerCancelledEventException, InputValidationException {

        Event event = createEvent(getValidEvent());
        Answer answer1 = null;
        try {

            // Answer event
            answer1 = eventService.answerEvent(event.getEventId(), VALID_EMPLOYEE_EMAIL, ATTENDING);

            //Answer again
            assertThrows(AlreadyAnsweredException.class, () -> {
                Answer answer2 = eventService.answerEvent(event.getEventId(), VALID_EMPLOYEE_EMAIL, ATTENDING);
                removeAnswer(answer2.getAnswerId());
            });

        } finally {
            // Clear database: remove answer (if created) and event
            if (answer1 != null) {
                removeAnswer(answer1.getAnswerId());
            }
            removeEvent(event.getEventId());
        }

    }

    @Test
    public void testAnswerLessThanOneDayBefore() {

        Event event = createEvent(getValidEvent());
        LocalDateTime celebrationDate = LocalDateTime.now().withNano(0).plusHours(2);
        event.setCelebrationDate(celebrationDate);
        updateEvent(event);
        try {
            assertThrows(AnswerExpiredException.class, () -> {
                Answer answer = eventService.answerEvent(event.getEventId(), VALID_EMPLOYEE_EMAIL, ATTENDING);
                removeAnswer(answer.getAnswerId());
            });
        } finally {
            // Clear database
            removeEvent(event.getEventId());
        }
    }

    @Test
    public void findEmployeeAnswersWithInvalidEmail() {
        assertThrows(InputValidationException.class, () -> eventService.findEmployeeAnswers(INVALID_EMPLOYEE_EMAIL, false));
    }

    @Test
    public void findEmployeeAnswers() throws InputValidationException {

        // Add answers
        List<Answer> answers = new LinkedList<Answer>();
        List<Answer> affirmativeAnswers = new LinkedList<Answer>();
        List<Event> events = new LinkedList<Event>();
        Event event1 = createEvent(getValidEvent("Event 1"));
        events.add(event1);
        Event event2 = createEvent(getValidEvent("Event 2"));
        events.add(event2);
        Event event3 = createEvent(getValidEvent("Event 3"));
        events.add(event3);
        Answer answer1 = createAnswer(VALID_EMPLOYEE_EMAIL, event1.getEventId(), ATTENDING);
        answers.add(answer1);
        affirmativeAnswers.add(answer1);
        Answer answer2 = createAnswer(VALID_EMPLOYEE_EMAIL, event2.getEventId(), NOT_ATTENDING);
        answers.add(answer2);
        Answer answer3 = createAnswer(VALID_EMPLOYEE_EMAIL, event3.getEventId(), ATTENDING);
        answers.add(answer3);
        affirmativeAnswers.add(answer3);
        Answer answer4 = createAnswer("user2@udc.es", event3.getEventId(), ATTENDING);

        try {
            List<Answer> foundAnswers = eventService.findEmployeeAnswers(VALID_EMPLOYEE_EMAIL, false);
            assertEquals(answers, foundAnswers);

            List<Answer> foundAnswersAffirmative = eventService.findEmployeeAnswers(VALID_EMPLOYEE_EMAIL, true);
            assertEquals(affirmativeAnswers, foundAnswersAffirmative);

            foundAnswers = eventService.findEmployeeAnswers("user3@udc.es", false);
            assertEquals(0, foundAnswers.size());
        } finally {
            // Clear Database
            removeAnswer(answer4.getAnswerId());
            for (Answer answer : answers) {
                removeAnswer(answer.getAnswerId());
            }
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }

    }

    @Test
    public void testCancelEvent()
            throws InstanceNotFoundException, AlreadyCanceledException, EventAlreadyCelebratedException {

        Event event = createEvent(getValidEvent());
        try {
            eventService.cancelEvent(event.getEventId());
            Event foundEvent=eventService.findEvent(event.getEventId());
            assertTrue(foundEvent.isCanceled());

        } finally {
            removeEvent(event.getEventId());
        }

    }

    @Test
    public void testCancelANotFoundEvent() {
        assertThrows(InstanceNotFoundException.class, () -> {
            eventService.cancelEvent(NON_EXISTENT_EVENT_ID);
        });
    }
    @Test
    public void testCancelACanceledEvent() {

        Event event = createEvent(getValidEvent());
        event.setCanceled(true);
        updateEvent(event);
        try {
            assertThrows(AlreadyCanceledException.class, () -> {
                eventService.cancelEvent(event.getEventId());
            });
        } finally {
            // Clear database
            removeEvent(event.getEventId());
        }
    }

    @Test
    public void testCancelACelebratedEvent() throws InterruptedException {

        Event event = createEvent(getValidEvent());
        LocalDateTime celebrationDate = LocalDateTime.now().withNano(0).plusSeconds(1);
        event.setCelebrationDate(celebrationDate);
        updateEvent(event);
        TimeUnit.SECONDS.sleep(3);
        try {
            assertThrows(EventAlreadyCelebratedException.class, () -> {
                eventService.cancelEvent(event.getEventId());
            });
        } finally {
            // Clear database
            removeEvent(event.getEventId());
        }
    }

    private Event getValidEventWithCelebrationDateAndDescription(String name, LocalDateTime date, String description) {
        return new Event(name, description, date, 19.95F);
    }

    @Test
    public void testFindEvents() {

        List<Event> events = new LinkedList<Event>();
        Event event1 = createEvent(getValidEventWithCelebrationDateAndDescription("Evento1",LocalDateTime.of(2023, 12, 23, 12, 0).withNano(0), "Descripcion 1"));
        events.add(event1);
        Event event2 = createEvent(getValidEventWithCelebrationDateAndDescription("Evento2",LocalDateTime.of(2023, 12, 24, 12, 0).withNano(0),"Descripcion 2"));
        events.add(event2);
        Event event3 = createEvent(getValidEventWithCelebrationDateAndDescription("Evento3", LocalDateTime.of(2023, 12, 25, 12, 0).withNano(0), "Hola 3"));
        events.add(event3);

        List<Event> events2 = new LinkedList<Event>();
        events2.add(event1);
        events2.add(event2);

        List<Event> events3 = new LinkedList<Event>();

        List<Event> events4 = new LinkedList<Event>();
        events4.add(event1);

        try {
            //Comprobar por fechas
            //Para que aparezcan todos los eventos
            LocalDateTime endDay = LocalDateTime.of(2023, 12, 26, 12, 0).withNano(0);//Por defecto todos los eventos se crean el 25, ponemos 26 como endday para que aparezcan todos
            List<Event> foundEvents = eventService.findEvents(LocalDateTime.now().withNano(0), endDay, null);
            assertEquals(events, foundEvents);

            //Para que solo aparezcan unos cuantos
            LocalDateTime endDay2 = LocalDateTime.of(2023, 12, 24, 12, 0).withNano(0);//Por defecto todos los eventos se crean el 25, ponemos 26 como endday para que aparezcan todos
            List<Event> foundEvents2 = eventService.findEvents(LocalDateTime.now().withNano(0), endDay2, null);
            assertEquals(events2, foundEvents2);

            //Para que no aparezca ninguno
            LocalDateTime endDay3 = LocalDateTime.of(2023, 12, 20, 12, 0).withNano(0);//ponemos 20 como endday para que no aparezca ninguno
            List<Event> foundEvents3 = eventService.findEvents(LocalDateTime.now().withNano(0), endDay3, "");
            assertEquals(events3, foundEvents3);

            //Comprobar por palabra clave
            //Para que aparezcan todos los eventos
            List<Event> foundEvents4 = eventService.findEvents(LocalDateTime.now().withNano(0), endDay, "Descripcion");
            assertEquals(events2, foundEvents4);

            //Para que solo aparezca uno concreto
            List<Event> foundEvents5 = eventService.findEvents(LocalDateTime.now().withNano(0), endDay, "Descripcion 1");
            assertEquals(events4, foundEvents5);

            //Para que no aparezca ninguno
            List<Event> foundEvents6 = eventService.findEvents(LocalDateTime.now().withNano(0), endDay, "ajsdfhsakljhdsdkl");
            assertEquals(events3, foundEvents6);

            LocalDateTime startDay = LocalDateTime.of(2023, 12, 24, 0, 0).withNano(0);
            LocalDateTime endDay4 = LocalDateTime.of(2023, 12, 25, 0, 0).withNano(0);
            List<Event> foundEvents7 = eventService.findEvents(startDay, endDay4, null);
            assertEquals(event2, foundEvents7.get(0));
            assertEquals(1, foundEvents7.size());

            LocalDateTime endDay5 = LocalDateTime.of(2023, 12, 20, 12, 0).withNano(0);//Si ponemos fecha de inicio posterior a fecha fin -> lista vacía
            assertThrows(InputValidationException.class, () -> { eventService.findEvents( endDay5, LocalDateTime.now().withNano(0),null);});


        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        } finally {
            // Clear Database
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }

    }


}