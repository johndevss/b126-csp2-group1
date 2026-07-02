package com.barangay_healthcare_appointment_system.controller;

import java.time.LocalDate;
import java.util.List;

import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.ConsultationRecord;
import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.service.AuthService;
import com.barangay_healthcare_appointment_system.service.ConsultationService;

public class ProviderController {
    private final AuthService authService;
    private final ConsultationService consultationService;

    public ProviderController() {
        this.authService = new AuthService();
        this.consultationService = new ConsultationService();
    }

    /**
     * Coordinates provider authentication.
     * @return User object if successful and role matches medical staff, null otherwise.
     */
    public User handleLogin(String username, String password) {
        User user = authService.login(username, password);
        
        // Ensure only medical staff (Doctor, Midwife, Nutritionist) can access this workflow
        if (user != null && !"staff".equalsIgnoreCase(user.getRole()) && !"admin".equalsIgnoreCase(user.getRole())) {
            return user;
        }
        
        return null; // Credential failure or unauthorized role
    }

    //Fetches today's real-time waiting queue for the logged-in doctor/provider.

    public List<Appointment> getWaitingQueue(int providerId) {
        // Automatically checks for today's date
        return consultationService.getQueueForProvider(providerId, LocalDate.now());
    }

    /**
     * Submits checkup text notes and closes out the active appointment row.
     * @throws IllegalArgumentException or IllegalStateException if the target ticket status isn't 'arrived'
     */
    public ConsultationRecord completeConsultation(int appointmentId, int providerId, String notes) {
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Consultation notes cannot be empty.");
        }
        
        // Hands data off to the service layer to save clinical notes and flip the appointment status to 'consulted'
        return consultationService.markConsulted(appointmentId, providerId, notes);
    }
}