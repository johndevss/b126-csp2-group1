package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.Patient;
import com.barangay_healthcare_appointment_system.repository.PatientRepository;
import java.time.LocalDate;

public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService() {
        // Instantiate the repository to handle database operations
        this.patientRepository = new PatientRepository();
    }

    /**
     * Validates input fields and saves a new patient profile.
     */
    public Patient registerPatient(String firstName, String middleName, String lastName,
                                   LocalDate dateOfBirth, String sex, String address,
                                   String familyNumber, String contactNumber) {
        
        // Null checker
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name are required.");
        }
        
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number is required for appointment alerts.");
        }

        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date of birth.");
        }

        // Map to data fields
        Patient newPatient = new Patient(firstName, middleName, lastName, dateOfBirth, 
                                         sex, address, familyNumber, contactNumber);

        // Save the new patient to the database
        return patientRepository.save(newPatient);
    }
}