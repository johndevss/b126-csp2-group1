package com.barangay_healthcare_appointment_system.view;

import java.util.Scanner;

public class MainMenuView {
    
    private final Scanner scanner;

    public MainMenuView() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=============================================");
            System.out.println("   BARANGAY HEALTHCARE APPOINTMENT SYSTEM    ");
            System.out.println("=============================================");
            System.out.println("Welcome! Please select your role:");
            System.out.println("[1] Patient (Book an Appointment)");
            System.out.println("[2] Clinic Staff");
            System.out.println("[3] Healthcare Provider (Doctor / Midwife)");
            System.out.println("[4] Admin");
            System.out.println("[0] Exit System");
            System.out.println("=============================================");
            System.out.print("Enter your choice: ");

            // Read the user's input as a raw string to prevent input mismatch crashes
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n--- Routing to Patient Portal ---");
                    PatientView patientView = new PatientView(scanner);
                    patientView.displayMenu();
                    break;
                case "2":
                    System.out.println("\n--- Routing to Staff Desk ---");
                    StaffView staffView = new StaffView(scanner);
                    staffView.displayMenu();
                    break;
                case "3":
                    System.out.println("\n--- Routing to Provider Dashboard ---");
                    ProviderView providerView = new ProviderView(scanner);
                    providerView.displayMenu();
                    break;
                case "4":
                    System.out.println("\n--- Routing to Admin Portal ---");
                    AdminView adminView = new AdminView(scanner);
                    adminView.displayMenu();
                    break;
                case "0":
                    System.out.println("\nShutting down system. Stay safe and healthy!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please enter 0, 1, 2, or 3.");
            }
        }
        
        scanner.close();
    }
}