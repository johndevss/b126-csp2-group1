package com.barangay_healthcare_appointment_system.model;

import java.time.LocalDateTime;

public class ConsultationRecord {

    private int id;
    private int appointmentId;
    private int recordedBy;
    private String notes;
    private LocalDateTime consultedAt;


    public ConsultationRecord(int appointmentId, int recordedBy, String notes) {
        this.appointmentId = appointmentId;
        this.recordedBy = recordedBy;
        this.notes = notes;
        this.consultedAt = LocalDateTime.now();
    }

    public ConsultationRecord(int id, int appointmentId, int recordedBy,
                               String notes, LocalDateTime consultedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.recordedBy = recordedBy;
        this.notes = notes;
        this.consultedAt = consultedAt;
    }

    public int getId() {
        return id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getRecordedBy() {
        return recordedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getConsultedAt() {
        return consultedAt;
    }

    @Override
    public String toString() {
        return "ConsultationRecord{appointmentId=" + appointmentId +
                ", consultedAt=" + consultedAt + "}";
    }
}