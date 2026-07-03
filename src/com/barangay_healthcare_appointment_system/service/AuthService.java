package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt; // 1. Import the BCrypt library

public class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Checks credentials against the database using BCrypt.
     * @return The authenticated User object if valid, or null if login fails.
     */
    public User login(String username, String plainTextPassword) {
        // Fetch the user purely by their username from the database
        User user = userRepository.findByUsername(username);

        // If the user exists, use BCrypt to verify the typed password against the hashed DB password
        if (user != null && BCrypt.checkpw(plainTextPassword, user.getPassword())) {
            return user; // Login successful!
        }

        return null; // Login failed (wrong username or password)
    }
}