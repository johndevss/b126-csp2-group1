package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.ConsultationRecord;
import com.barangay_healthcare_appointment_system.repository.AppointmentRepository;
import com.barangay_healthcare_appointment_system.repository.ConsultationRecordRepository;
import java.time.LocalDate;
import java.util.List;

public class ConsultationService {
    private final ConsultationRecordRepository consultationRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public ConsultationService() {
        this.consultationRecordRepository = new ConsultationRecordRepository();
        this.appointmentRepository = new AppointmentRepository();
    }

    //Fetches the real-time active waiting queue for a specific healthcare provider.
    public List<Appointment> getQueueForProvider(int providerId, LocalDate date) {
        // Pulls records from the database where assigned_to matches and status is strictly 'arrived'
        return appointmentRepository.findQueueForProvider(providerId, date);
    }

    //Records medical checkup notes and flags the overall appointment lifecycle as 'consulted'.
    public ConsultationRecord markConsulted(int appointmentId, int recordedBy, String notes) {
        // Check if appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment ID not found.");
        }

        // Validate if the correct status is for consultation
        if (!"arrived".equals(appointment.getStatus())) {
            throw new IllegalStateException("Can only consult patients who are currently marked as 'arrived'.");
        }

        // 3. Create a new consultation record and save it to the database
        ConsultationRecord record = new ConsultationRecord(appointmentId, recordedBy, notes);
        ConsultationRecord savedRecord = consultationRecordRepository.save(record);

        if (savedRecord != null) {
            // 4. Update the parent appointment lifecycle column status to 'consulted'
            appointmentRepository.updateStatus(appointmentId, "consulted");
        }

        return savedRecord;
    }
}