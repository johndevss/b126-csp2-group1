package com.barangay_healthcare_appointment_system.controller;

import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.Patient;
import com.barangay_healthcare_appointment_system.service.AppointmentService;
import com.barangay_healthcare_appointment_system.service.PatientService;
import java.time.LocalDate;

public class PatientController {
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController() {
        // Initialize the services this controller relies on
        this.patientService = new PatientService();
        this.appointmentService = new AppointmentService();
    }

    public String handleBooking(String firstName, String middleName, String lastName,
                                LocalDate dateOfBirth, String sex, String address,
                                String familyNumber, String contactNumber,
                                int serviceId, LocalDate appointmentDate) {
        
        // If names are blank or missing, the service throws an error which bubbles up to the view
        Patient newPatient = patientService.registerPatient(
            firstName, middleName, lastName, dateOfBirth, sex, address, familyNumber, contactNumber
        );
        
        // Grab the auto-generated database ID from the new patient row
        int assignedPatientId = newPatient.getId();
        
        // Pass that patient ID, the service choice, and target date to the AppointmentService
        Appointment newAppointment = appointmentService.bookAppointment(
            assignedPatientId, serviceId, appointmentDate
        );
        
        // Return the unique tracking string back to the View layer to print out for the patient
        return newAppointment.getAppointmentCode();
    }
}