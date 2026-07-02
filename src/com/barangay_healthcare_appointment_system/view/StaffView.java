package com.barangay_healthcare_appointment_system.view;

import com.barangay_healthcare_appointment_system.controller.StaffController;
import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.User;
import java.util.List;
import java.util.Scanner;

public class StaffView {
    private final Scanner scanner;
    private final StaffController staffController;
    private User loggedInStaff;

    public StaffView(Scanner scanner) {
        this.scanner = scanner;
        this.staffController = new StaffController();
    }

    public void displayMenu() {
        System.out.println("\n---------------------------------------------");
        System.out.println("          STAFF / ADMIN SECURE GATEWAY       ");
        System.out.println("---------------------------------------------");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        // Pass credentials to backend
        loggedInStaff = staffController.handleLogin(username, password);

        if (loggedInStaff == null) {
            System.out.println("\n[!] Access Denied: Invalid credentials or unauthorized role.");
            return; // Exit out back to Main Menu
        }

        runStaffDashboard();
    }

    private void runStaffDashboard() {
        boolean inDashboard = true;
        while (inDashboard) {
            System.out.println("\n=============================================");
            System.out.println("📋 WELCOME, STAFF: " + loggedInStaff.getFullName().toUpperCase());
            System.out.println("Role: " + loggedInStaff.getRole().toUpperCase());
            System.out.println("=============================================");
            System.out.println("[1] View Today's Master Schedule Dashboard");
            System.out.println("[2] Check-In Arriving Patient");
            System.out.println("[0] Logout");
            System.out.print("Enter command choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showTodayDashboard();
                    break;
                case "2":
                    processCheckIn();
                    break;
                case "0":
                    System.out.println("\nClearing secure session... logged out successfully.");
                    loggedInStaff = null;
                    inDashboard = false;
                    break;
                default:
                    System.out.println("\n[!] Unknown choice. Please enter 1, 2, or 0.");
            }
        }
    }


    private void showTodayDashboard() {
        System.out.println("\n--- Today's Master Appointment List ---");
        List<Appointment> todayList = staffController.getTodayDashboard();

        if (todayList.isEmpty()) {
            System.out.println("📭 No appointments scheduled for today.");
            return;
        }

        // Clean table grid layout
        System.out.printf("%-6s | %-15s | %-10s | %-10s\n", "ID", "TRACKING CODE", "SERVICE ID", "STATUS");
        System.out.println("---------------------------------------------------------");
        for (Appointment app : todayList) {
            System.out.printf("%-6d | %-15s | %-10d | %-10s\n", 
                app.getId(), 
                app.getAppointmentCode(), 
                app.getServiceId(), 
                app.getStatus().toUpperCase()
            );
        }
    }

    // Reads a unique tracking string to change a patient's attendance status.

    private void processCheckIn() {
        System.out.println("\n--- Walk-In Check-In Intake ---");
        System.out.print("Scan or Type Patient Tracking Code (e.g., HC-2026-0001): ");
        String code = scanner.nextLine().trim();

        if (code.isEmpty()) {
            System.out.println("[!] Cancelled: Tracking code input cannot be empty.");
            return;
        }

        boolean success = staffController.handleCheckIn(code);

        if (success) {
            System.out.println("\n=============================================");
            System.out.println("✅ PATIENT CHECKED IN SUCCESSFULLY!");
            System.out.println("Ticket status for " + code + " changed to 'arrived'.");
            System.out.println("Patient has been forwarded to the waiting room queue.");
            System.out.println("=============================================");
        } else {
            System.out.println("\n❌ CHECK-IN REJECTED");
            System.out.println("Reason: Code not found, or appointment is not in 'pending' status.");
        }
    }
}