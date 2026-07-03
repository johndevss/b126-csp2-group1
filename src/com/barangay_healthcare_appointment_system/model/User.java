package com.barangay_healthcare_appointment_system.model;

public class User {

    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String username;
    private String password;  
    private String role;

    public User() {
    }

    // NEW user constructor no id yet
    public User(String firstName, String middleName, String lastName,
                String username, String password, String role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // EXISTING user constructor loaded from a DB row
    public User(int id, String firstName, String middleName, String lastName,
                String username, String password, String role) {
        this(firstName, middleName, lastName, username, password, role);
        this.id = id;
    }

    // --- GETTERS ---
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        if (middleName == null || middleName.trim().isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + middleName + " " + lastName;
    }

    // --- SETTERS ---
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}