package es.udc.ws.app.model.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private float duration;
    private LocalDateTime creationDate;
    private boolean canceled;
    private int employeesAttending;
    private int employeesNotAttending;

    public Event(String name, String description, LocalDateTime celebrationDate, float duration) {
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
        this.canceled = false;
        this.employeesAttending = 0;
        this.employeesNotAttending = 0;
    }

    /*AÑADIDO PARA DTOS--------------------------*/
    public Event(Long eventId, String name, String description, LocalDateTime celebrationDate, float duration, boolean canceled, int employeesAttending, int employeesNotAttending) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
        this.canceled = canceled;
        this.employeesAttending = employeesAttending;
        this.employeesNotAttending = employeesNotAttending;
    }
    /*-------------------------------------------*/

    public Event(Long eventId, String name, String description, LocalDateTime celebrationDate, float duration,
                 LocalDateTime creationDate, boolean canceled, int employeesAttending, int employeesNotAttending) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
        this.canceled = canceled;
        this.employeesAttending = employeesAttending;
        this.employeesNotAttending = employeesNotAttending;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public int getEmployeesAttending() {
        return employeesAttending;
    }

    public void setEmployeesAttending(int employeesAttending) {
        this.employeesAttending = employeesAttending;
    }

    public int getEmployeesNotAttending() {
        return employeesNotAttending;
    }

    public void setEmployeesNotAttending(int employeesNotAttending) {
        this.employeesNotAttending = employeesNotAttending;
    }
    public void increaseEmployeesAttending() {
        this.employeesAttending++;
    }
    public void increaseEmployeesNotAttending() {
        this.employeesNotAttending++;
    }

    public Long getEventId() {
        return eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Float.compare(event.duration, duration) == 0 && canceled == event.canceled && employeesAttending == event.employeesAttending && employeesNotAttending == event.employeesNotAttending && eventId.equals(event.eventId) && name.equals(event.name) && description.equals(event.description) && celebrationDate.equals(event.celebrationDate) && creationDate.equals(event.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, description, celebrationDate, duration, creationDate, canceled, employeesAttending, employeesNotAttending);
    }
}