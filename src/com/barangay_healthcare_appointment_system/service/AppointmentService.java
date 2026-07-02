package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.repository.AppointmentRepository;
import java.time.LocalDate;
import java.util.List;

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ScheduleService scheduleService;

    public AppointmentService() {
        this.appointmentRepository = new AppointmentRepository();
        this.scheduleService = new ScheduleService(); // Used to check day-of-week rules
    }

    // Enforces schedule rules and books an appointment if valid.
    public Appointment bookAppointment(int patientId, int serviceId, LocalDate appointmentDate) {
        // Checks if the booked date has already passed
        if (appointmentDate == null || appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }

        // ENforce day-of-week rules according to the barangay health center
        boolean isAvailable = scheduleService.isServiceAvailableOnDate(serviceId, appointmentDate);
        if (!isAvailable) {
            throw new IllegalStateException("The selected service is not available on this day of the week.");
        }

        Appointment appointment = new Appointment(patientId, serviceId, appointmentDate);

        // Generate tracking code for user
        String trackingCode = generateAppointmentCode(appointmentDate);
        appointment.setAppointmentCode(trackingCode);

        // Save to database
        return appointmentRepository.save(appointment);
    }

    // Generates a unique appointment code based on the year and a sequential number.
    private String generateAppointmentCode(LocalDate date) {
        int currentCount = appointmentRepository.countAll();
        int nextSequenceNumber = currentCount + 1;
        // Format layout: HC-[YEAR]-[4-DIGIT PADDED NUMBER] -> e.g., HC-2026-0001
        return String.format("HC-%d-%04d", date.getYear(), nextSequenceNumber);
    }

    //Marks a patient as "arrived" when they present their code at the clinic desk.
    public boolean checkInPatient(String appointmentCode) {
        if (appointmentCode == null || appointmentCode.isBlank()) {
            return false;
        }

        // Look up the appointment record using the tracking code
        Appointment appointment = appointmentRepository.findByAppointmentCode(appointmentCode);
        if (appointment == null) {
            System.err.println("Booking code not found in system.");
            return false;
        }

        // Enforce state machine logic: Can only check-in a "pending" appointment
        if (!"pending".equals(appointment.getStatus())) {
            System.err.println("Appointment cannot be checked in. Current status: " + appointment.getStatus());
            return false;
        }

        // Update the status row to 'arrived' so they land on the medical provider's queue
        return appointmentRepository.updateStatus(appointment.getId(), "arrived");
    }

    public List<Appointment> getTodayList(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }
}