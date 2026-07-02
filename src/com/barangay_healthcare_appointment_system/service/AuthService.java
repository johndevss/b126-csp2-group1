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
        // Quick input guard
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        // Fetch user from the DB by their unique username
        User user = userRepository.findByUsername(username);

        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            return user; 
        }

        return null;
    }
}