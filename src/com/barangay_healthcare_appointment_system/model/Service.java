package com.barangay_healthcare_appointment_system.model;

public class Service {

    private int id;
    private String name;

    public Service(String name) {
        this.name = name;
    }

    public Service(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Service{id=" + id + ", name='" + name + "'}";
    }
}