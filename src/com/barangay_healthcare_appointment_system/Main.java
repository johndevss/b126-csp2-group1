package com.barangay_healthcare_appointment_system;

import com.barangay_healthcare_appointment_system.view.MainMenuView;
import com.barangay_healthcare_appointment_system.config.DBConnection;


public class Main {
    public static void main(String[] args) {
        DBConnection.testConnection();
        MainMenuView app = new MainMenuView();
        
        app.start();
    }
}