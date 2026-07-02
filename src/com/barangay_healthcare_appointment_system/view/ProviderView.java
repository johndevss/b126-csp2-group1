package com.barangay_healthcare_appointment_system.view;

import com.barangay_healthcare_appointment_system.controller.ProviderController;
import com.barangay_healthcare_appointment_system.model.Appointment;
import com.barangay_healthcare_appointment_system.model.User;
import java.util.List;
import java.util.Scanner;

public class ProviderView {
    private final Scanner scanner;
    private final ProviderController providerController;
    private User loggedInProvider;

    public ProviderView(Scanner scanner) {
        this.scanner = scanner;
        this.providerController = new ProviderController();
    }


    public void displayMenu() {
        System.out.println("\n---------------------------------------------");
        System.out.println("         PROVIDER SECURE GATEWAY             ");
        System.out.println("---------------------------------------------");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        // Attempt login via the controller layer
        loggedInProvider = providerController.handleLogin(username, password);

        if (loggedInProvider == null) {
            System.out.println("\n[!] Access Denied: Invalid credentials or unauthorized role.");
            return; // Kicks them back to the Main Menu
        }

        runProviderDashboard();
    }

    private void runProviderDashboard() {
        boolean inDashboard = true;
        while (inDashboard) {
            System.out.println("\n=============================================");
            System.out.println("🩺 WELCOME, DR./PROVIDER: " + loggedInProvider.getFullName().toUpperCase());
            System.out.println("Role: " + loggedInProvider.getRole().toUpperCase());
            System.out.println("=============================================");
            System.out.println("[1] View My Today's Waiting Queue");
            System.out.println("[2] Process Patient Consultation Notes");
            System.out.println("[0] Logout & Exit Dashboard");
            System.out.print("Enter command choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showWaitingQueue();
                    break;
                case "2":
                    handleConsultationForm();
                    break;
                case "0":
                    System.out.println("\nLogging out user session... secure session cleared.");
                    loggedInProvider = null;
                    inDashboard = false;
                    break;
                default:
                    System.out.println("\n[!] Unknown choice. Please enter 1, 2, or 0.");
            }
        }
    }

    private void showWaitingQueue() {
        System.out.println("\n--- Real-Time Active Queue ---");
        List<Appointment> queue = providerController.getWaitingQueue(loggedInProvider.getId());

        if (queue.isEmpty()) {
            System.out.println("✨ Hooray! Your waiting queue is currently empty.");
            return;
        }

        // Print out a scannable grid layout for the doctor
        System.out.printf("%-15s | %-18s | %-12s\n", "APPOINTMENT ID", "TRACKING CODE", "STATUS");
        System.out.println("---------------------------------------------");
        for (Appointment app : queue) {
            System.out.printf("%-15d | %-18s | %-12s\n", 
                app.getId(), 
                app.getAppointmentCode(), 
                app.getStatus().toUpperCase()
            );
        }
    }

    private void handleConsultationForm() {
        System.out.println("\n--- Patient Evaluation & Notes Form ---");
        System.out.print("Enter target Appointment ID to evaluate: ");
        String idInput = scanner.nextLine().trim();

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("[!] Cancelled: Please enter a valid numerical ID number.");
            return;
        }

        System.out.println("Enter medical findings/prescriptions (Press enter when finished):");
        System.out.print("👉 Notes: ");
        String notes = scanner.nextLine().trim();

        try {
            providerController.completeConsultation(appointmentId, loggedInProvider.getId(), notes);
            
            System.out.println("\n=============================================");
            System.out.println("✅ CONSULTATION CLOSED & ARCHIVED!");
            System.out.println("Appointment ID #" + appointmentId + " updated to 'consulted'.");
            System.out.println("=============================================");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n❌ ERROR SAVING EVALUATION RECORD");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}