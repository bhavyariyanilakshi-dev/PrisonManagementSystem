package com.prison.model;

public class Staff {
    private int id;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private String username;
    private String passwordHash;
    private String salt;

    // Constructors
    public Staff() {}

    public Staff(int id, String firstName, String lastName, String position,
                 String department, String username, String passwordHash, String salt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + position + ")";
    }
}
