package com.prison.model;

import java.util.Date;

public class LegalEvent {
    private int id;
    private int prisonerId;
    private String eventType;
    private Date eventDate;
    private String description;

    // Constructors
    public LegalEvent() {}

    public LegalEvent(int id, int prisonerId, String eventType, Date eventDate, String description) {
        this.id = id;
        this.prisonerId = prisonerId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrisonerId() { return prisonerId; }
    public void setPrisonerId(int prisonerId) { this.prisonerId = prisonerId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return eventType + " (" + eventDate + ")";
    }
}
