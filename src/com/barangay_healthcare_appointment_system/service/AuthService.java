package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Checks credentials against the database.
     * @return The authenticated User object if valid, or null if login fails.
     */
    public User login(String username, String password) {
        User user = userRepository.findByCredentials(username, password);

        if (user != null) {
            return user; // Login successful!
        }

        return null; // Login failed
    }
}