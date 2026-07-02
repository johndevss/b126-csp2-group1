package com.barangay_healthcare_appointment_system.model;

public class ServiceSchedule {

    private int id;
    private int serviceId;      
    private String dayOfWeek;

    // NEW schedule entry — no id yet
    public ServiceSchedule(int serviceId, String dayOfWeek) {
        this.serviceId = serviceId;
        this.dayOfWeek = dayOfWeek;
    }

    // EXISTING schedule entry — loaded from a DB row
    public ServiceSchedule(int id, int serviceId, String dayOfWeek) {
        this(serviceId, dayOfWeek);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "ServiceSchedule{serviceId=" + serviceId + ", dayOfWeek='" + dayOfWeek + "'}";
    }
}