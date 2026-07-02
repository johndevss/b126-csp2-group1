package com.barangay_healthcare_appointment_system.controller;

import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.service.AppointmentService;
import com.barangay_healthcare_appointment_system.service.AuthService;

import java.time.LocalDate;
import java.util.List;

public class StaffController {
    private final AuthService authService;
    private final AppointmentService appointmentService;

    public StaffController() {
        // Instantiate the services needed for desk operations
        this.authService = new AuthService();
        this.appointmentService = new AppointmentService();
    }

    public User handleLogin(String username, String password) {
        User user = authService.login(username, password);

        // Ensure only front-desk staff or admins can access this workflow
        if (user != null && ("staff".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getRole()))) {
            return user;
        }

        return null; // Credential failure or unauthorized role
    }

    //Fetches the entire master list of scheduled appointments for today.

    public List<Appointment> getTodayDashboard() {
        // Passes today's date down to the service layer
        return appointmentService.getTodayList(LocalDate.now());
    }

    /**
     * Processes a patient arrival at the front desk.
     * @return true if the check-in succeeded, false if the code was invalid or status wasn't 'pending'.
     */
    public boolean handleCheckIn(String appointmentCode) {
        return appointmentService.checkInPatient(appointmentCode);
    }
}