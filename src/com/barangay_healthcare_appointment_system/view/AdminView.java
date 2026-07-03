package com.barangay_healthcare_appointment_system.view;

import com.barangay_healthcare_appointment_system.controller.AdminController;
import com.barangay_healthcare_appointment_system.controller.StaffController;
import com.barangay_healthcare_appointment_system.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminView {
    private final Scanner scanner;
    private final AdminController adminController;
    private final StaffController authController;
    private User loggedInAdmin;

    public AdminView(Scanner scanner) {
        this.scanner = scanner;
        this.adminController = new AdminController();
        this.authController = new StaffController(); 
    }

    public void displayMenu() {
        System.out.println("\n---------------------------------------------");
        System.out.println("         SYSTEM ADMINISTRATOR PORTAL         ");
        System.out.println("---------------------------------------------");
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine().trim();

        // Verify credentials using our existing auth logic
        loggedInAdmin = authController.handleLogin(username, password);

        if (loggedInAdmin == null || !loggedInAdmin.getRole().equalsIgnoreCase("admin")) {
            System.out.println("\n[!] Access Denied: Invalid credentials or you do not have Administrator privileges.");
            return; 
        }

        runAdminDashboard();
    }

    private void runAdminDashboard() {
        boolean inDashboard = true;
        while (inDashboard) {
            System.out.println("\n=============================================");
            System.out.println("⚙️  WELCOME, MASTER ADMIN: " + loggedInAdmin.getFullName().toUpperCase());
            System.out.println("=============================================");
            System.out.println("[1] View All Clinic Staff");
            System.out.println("[2] Register New Employee");
            System.out.println("[3] Update Employee Details");
            System.out.println("[4] Force Reset Employee Password");
            System.out.println("[0] Logout & Exit");
            System.out.print("Enter command choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    displayAllStaff();
                    break;
                case "2":
                    registerStaff();
                    break;
                case "3":
                    updateStaff();
                    break;
                case "4":
                    resetPassword();
                    break;
                case "0":
                    System.out.println("\nLogging out... secure admin session closed.");
                    loggedInAdmin = null;
                    inDashboard = false;
                    break;
                default:
                    System.out.println("\n[!] Unknown choice. Please enter 0-4.");
            }
        }
    }

    private void displayAllStaff() {
        System.out.println("\n--- Current System Users ---");
        List<User> staff = adminController.getAllStaffList();
        
        System.out.printf("%-5s | %-15s | %-25s | %-10s\n", "ID", "USERNAME", "FULL NAME", "ROLE");
        System.out.println("------------------------------------------------------------------");
        for (User u : staff) {
            System.out.printf("%-5d | %-15s | %-25s | %-10s\n", 
                u.getId(), u.getUsername(), u.getFullName(), u.getRole().toUpperCase());
        }
    }

    private void registerStaff() {
        System.out.println("\n--- Register New Employee ---");
        System.out.print("First Name: ");
        String fName = scanner.nextLine().trim();
        System.out.print("Middle Name (leave blank if none): ");
        String mName = scanner.nextLine().trim();
        System.out.print("Last Name: ");
        String lName = scanner.nextLine().trim();
        
        System.out.print("Assign Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Assign Temporary Password: ");
        String pass = scanner.nextLine().trim();
        System.out.print("Assign Role (admin/staff/doctor/midwife/nutritionist): ");
        String role = scanner.nextLine().trim();

        boolean success = adminController.registerNewStaff(fName, mName, lName, user, pass, role);
        if (success) {
            System.out.println("✅ Registration successful! BCrypt hash generated and stored.");
        } else {
            System.out.println("❌ Registration failed. Username might be taken.");
        }
    }

    private void updateStaff() {
        System.out.println("\n--- Update Employee Record ---");
        System.out.print("Enter Target User ID: ");
        int targetId = parseIntegerInput();
        if (targetId == -1) return;

        System.out.print("New First Name: ");
        String fName = scanner.nextLine().trim();
        System.out.print("New Middle Name: ");
        String mName = scanner.nextLine().trim();
        System.out.print("New Last Name: ");
        String lName = scanner.nextLine().trim();
        System.out.print("New Role (admin/staff/doctor/midwife/nutritionist): ");
        String role = scanner.nextLine().trim();

        boolean success = adminController.updateStaffInfo(targetId, fName, mName, lName, role);
        if (success) {
            System.out.println("✅ Employee record updated successfully.");
        }
    }

    private void resetPassword() {
        System.out.println("\n--- Force Password Reset ---");
        System.out.print("Enter Target User ID: ");
        int targetId = parseIntegerInput();
        if (targetId == -1) return;

        System.out.print("Enter New Password: ");
        String newPass = scanner.nextLine().trim();

        boolean success = adminController.resetStaffPassword(targetId, newPass);
        if (success) {
            System.out.println("✅ Password reset securely. New BCrypt hash applied.");
        }
    }

    // Helper method to prevent the app from crashing if they type letters instead of an ID
    private int parseIntegerInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid input. Please enter a numerical ID.");
            return -1;
        }
    }
}