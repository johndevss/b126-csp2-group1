package com.barangay_healthcare_appointment_system;

import com.barangay_healthcare_appointment_system.config.DBConnection;

public class Main {
    public static void main(String[] args) {
        
        // Test the database connection
        DBConnection.testConnection();
    }
}