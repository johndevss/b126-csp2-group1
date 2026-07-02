package com.barangay_healthcare_appointment_system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {

    private int id;                    
    private String firstName;
    private String middleName;        
    private String lastName;
    private LocalDate dateOfBirth;
    private String sex;               
    private String address;
    private String familyNumber;
    private String contactNumber;
    private LocalDateTime createdAt;

    public Patient(String firstName, String middleName, String lastName,
                   LocalDate dateOfBirth, String sex, String address,
                   String familyNumber, String contactNumber) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.address = address;
        this.familyNumber = familyNumber;
        this.contactNumber = contactNumber;
    }

    public Patient(int id, String firstName, String middleName, String lastName,
                   LocalDate dateOfBirth, String sex, String address,
                   String familyNumber, String contactNumber, LocalDateTime createdAt) {
        this(firstName, middleName, lastName, dateOfBirth, sex, address, familyNumber, contactNumber);
        this.id = id;
        this.createdAt = createdAt;
    }

    // Getters

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public String getAddress() {
        return address;
    }

    public String getFamilyNumber() {
        return familyNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFamilyNumber(String familyNumber) {
        this.familyNumber = familyNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getFullName() {
        if (middleName == null || middleName.isBlank()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + middleName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", sex='" + sex + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}