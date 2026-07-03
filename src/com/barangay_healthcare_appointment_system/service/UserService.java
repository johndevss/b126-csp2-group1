package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // Handles business flow for registering a new system worker.

    public boolean registerUser(User user) {
        // Prevent duplicate usernames from breaking the database unique constraint
        if (userRepository.findByUsername(user.getUsername()) != null) {
            System.out.println("\n[!] Registration Denied: Username '" + user.getUsername() + "' is already taken.");
            return false;
        }

        // Send the packaged user record (with the BCrypt hashed password) down to the DB layer
        User savedUser = userRepository.save(user);
        return savedUser != null;
    }

    // Updates an existing staff member's core metadata details.

    public boolean updateUserDetails(int userId, String firstName, String middleName, String lastName, String role) {
        // Find existing database entity row first
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            System.out.println("\n[!] Update Aborted: No employee found with User ID #" + userId);
            return false;
        }

        // Create an updated user instance retaining the original credentials
        User updatedUser = new User(
            existingUser.getId(),
            firstName,
            middleName,
            lastName,
            existingUser.getUsername(),
            existingUser.getPassword(), // Keeps the existing BCrypt hash secure and untouched
            role.toLowerCase()
        );

        // Send the updated model down to the repository execution pipeline
        return userRepository.update(updatedUser);
    }

    // Updates/Resets an existing staff member's password token field.

    public boolean updateUserPassword(int userId, String newHashedPassword) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            System.out.println("\n[!] Password Reset Aborted: User ID #" + userId + " does not exist.");
            return false;
        }

        // Swap the old hash with the newly computed BCrypt hash string
        existingUser.setPassword(newHashedPassword);
        
        return userRepository.update(existingUser);
    }

    // Fetches all registered clinic workers. Useful for the admin view list lookups.
    public List<User> getAllWorkers() {
        return userRepository.findAll();
    }
}