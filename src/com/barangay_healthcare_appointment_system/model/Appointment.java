package com.barangay_healthcare_appointment_system.model;

import java.time.LocalDate;

public class Appointment {

    private int id;
    private String appointmentCode;   
    private int patientId;
    private int serviceId;
    private Integer assignedTo;
    private LocalDate appointmentDate;
    private String status;

    public Appointment(int patientId, int serviceId, LocalDate appointmentDate) {
        this.patientId = patientId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.status = "pending";
    }

    public Appointment(int id, String appointmentCode, int patientId, int serviceId,
                        Integer assignedTo, LocalDate appointmentDate, String status) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.patientId = patientId;
        this.serviceId = serviceId;
        this.assignedTo = assignedTo;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{code='" + appointmentCode + "', status='" + status +
                "', date=" + appointmentDate + "}";
    }
}