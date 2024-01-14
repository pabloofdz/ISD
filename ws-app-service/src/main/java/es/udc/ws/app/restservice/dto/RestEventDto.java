package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestEventDto {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private float duration;
    private boolean canceled;
    private int employeesAttending;
    private int totalAnswers;

    public RestEventDto (Long eventId, String name, String description, LocalDateTime celebrationDate, float duration, boolean canceled, int employeesAttending, int totalAnswers) {
        this.eventId=eventId;
        this.name=name;
        this.description=description;
        this.celebrationDate=celebrationDate;
        this.duration=duration;
        this.canceled=canceled;
        this.employeesAttending=employeesAttending;
        this.totalAnswers=totalAnswers;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public int getEmployeesAttending() {
        return employeesAttending;
    }

    public int getTotalAnswers() {
        return totalAnswers;
    }

    @Override
    public String toString() {
        return "EventDto [eventId=" + eventId + ", name=" + name
                + ", description=" + description
                + ", celebrationDate=" + celebrationDate + ", duration=" + duration +
                ", canceled=" + canceled +", employeesAttending=" + employeesAttending +
                ", totalAnswers=" + totalAnswers +"]";
    }
}
