package com.barangay_healthcare_appointment_system.controller;

import com.barangay_healthcare_appointment_system.model.User;
import com.barangay_healthcare_appointment_system.service.UserService;
import org.mindrot.jbcrypt.BCrypt; // Importing your new library!

public class AdminController {
    private final UserService userService;

    public AdminController() {
        // We will need a UserService to handle the actual database saving/updating
        this.userService = new UserService();
    }

    //Registers a new staff member and hashes their password before saving.

    public boolean registerNewStaff(String firstName, String middleName, String lastName, 
                                    String username, String plainPassword, String role) {
        
        // Hash the password using BCrypt. 
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // Package all the data into a User object
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setMiddleName(middleName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword); // Store hashed password
        newUser.setRole(role);

        // Send it to the database service
        return userService.registerUser(newUser);
    }

    public boolean updateStaffInfo(int userId, String firstName, String middleName, 
                                   String lastName, String role) {
        // Passes the updated text directly to the service layer
        return userService.updateUserDetails(userId, firstName, middleName, lastName, role);
    }

    public boolean resetStaffPassword(int userId, String newPlainPassword) {
        // Hash the new password before sending it to the database
        String newHashedPassword = BCrypt.hashpw(newPlainPassword, BCrypt.gensalt());
        
        return userService.updateUserPassword(userId, newHashedPassword);
    }

    // Fetches the complete list of clinic staff for the admin dashboard.
    public java.util.List<User> getAllStaffList() {
        return userService.getAllWorkers();
    }
}