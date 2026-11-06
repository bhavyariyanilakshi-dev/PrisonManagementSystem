package com.prison.model;

import java.util.Date;

public class Prisoner {
    private int id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String gender;
    private String crimeType;
    private Date sentenceStart;
    private Date sentenceEnd;
    private int cellId;
    private String healthStatus;
    private String behaviorStatus;

    // Constructors
    public Prisoner() {}

    public Prisoner(int id, String firstName, String lastName, Date dob, String gender,
                    String crimeType, Date sentenceStart, Date sentenceEnd, int cellId,
                    String healthStatus, String behaviorStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.crimeType = crimeType;
        this.sentenceStart = sentenceStart;
        this.sentenceEnd = sentenceEnd;
        this.cellId = cellId;
        this.healthStatus = healthStatus;
        this.behaviorStatus = behaviorStatus;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCrimeType() { return crimeType; }
    public void setCrimeType(String crimeType) { this.crimeType = crimeType; }

    public Date getSentenceStart() { return sentenceStart; }
    public void setSentenceStart(Date sentenceStart) { this.sentenceStart = sentenceStart; }

    public Date getSentenceEnd() { return sentenceEnd; }
    public void setSentenceEnd(Date sentenceEnd) { this.sentenceEnd = sentenceEnd; }

    public int getCellId() { return cellId; }
    public void setCellId(int cellId) { this.cellId = cellId; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getBehaviorStatus() { return behaviorStatus; }
    public void setBehaviorStatus(String behaviorStatus) { this.behaviorStatus = behaviorStatus; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
