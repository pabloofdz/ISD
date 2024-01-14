package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ClientEventDto {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private LocalDateTime endDate;
    private boolean canceled;
    private int employeesAttending;
    private int totalAnswers;


    public ClientEventDto (Long eventId, String name, String description, LocalDateTime celebrationDate, LocalDateTime endDate) {
        this.eventId=eventId;
        this.name=name;
        this.description=description;
        this.celebrationDate=celebrationDate;
        this.endDate=endDate;
        this.canceled=false;
        this.employeesAttending=0;
        this.totalAnswers=0;
    }
    public ClientEventDto (Long eventId, String name, String description, LocalDateTime celebrationDate, LocalDateTime endDate, boolean canceled, int employeesAttending, int totalAnswers) {
        this.eventId=eventId;
        this.name=name;
        this.description=description;
        this.celebrationDate=celebrationDate;
        this.endDate=endDate;
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

    public LocalDateTime getEndDate() {
        return endDate;
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
                + ", celebrationDate=" + celebrationDate + ", endDate=" + endDate +
                ", canceled=" + canceled +", employeesAttending=" + employeesAttending +
                ", totalAnswers=" + totalAnswers +"]";
    }
}