package com.barangay_healthcare_appointment_system.view;

import com.barangay_healthcare_appointment_system.controller.PatientController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PatientView {
    private final Scanner scanner;
    private final PatientController patientController;

    // Receive the single shared scanner instance from MainMenuView
    public PatientView(Scanner scanner) {
        this.scanner = scanner;
        this.patientController = new PatientController();
    }

    public void displayMenu() {
        boolean inPatientMenu = true;
        while (inPatientMenu) {
            System.out.println("\n---------------------------------------------");
            System.out.println("               PATIENT PORTAL                ");
            System.out.println("---------------------------------------------");
            System.out.println("[1] Book New Appointment");
            System.out.println("[0] Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                runBookingForm();
            } else if ("0".equals(choice)) {
                inPatientMenu = false;
            } else {
                System.out.println("\n[!] Invalid choice. Please enter 1 or 0.");
            }
        }
    }

    private void runBookingForm() {
        System.out.println("\n--- Patient Registration Form ---");
        
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter Middle Name (Leave blank if none): ");
        String middleName = scanner.nextLine().trim();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine().trim();

        // Gather and safely parse Date of Birth
        LocalDate dob = null;
        while (dob == null) {
            System.out.print("Enter Birth Date (YYYY-MM-DD): ");
            String dobInput = scanner.nextLine().trim();
            try {
                dob = LocalDate.parse(dobInput);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid date format. Use exactly YYYY-MM-DD (e.g., 1998-05-24).");
            }
        }

        System.out.print("Enter Sex (Male/Female): ");
        String sex = scanner.nextLine().trim();

        System.out.print("Enter Complete Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter Family Logbook/Household Number: ");
        String familyNumber = scanner.nextLine().trim();

        System.out.print("Enter Active Contact Number: ");
        String contactNumber = scanner.nextLine().trim();

        System.out.println("\n--- Available Health Center Services ---");
        System.out.println("[1] General Checkup (Mon / Fri)");
        System.out.println("[2] Baby Vaccination (Tue Only)");
        System.out.println("[3] Prenatal Check-up (Wed Only)");
        
        // Gather and validate service ID input number
        int serviceId = 0;
        while (serviceId < 1 || serviceId > 3) {
            System.out.print("Select Service ID (1-3): ");
            String serviceInput = scanner.nextLine().trim();
            try {
                serviceId = Integer.parseInt(serviceInput);
                if (serviceId < 1 || serviceId > 3) {
                    System.out.println("[!] Please choose a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid reference number.");
            }
        }

        // Gather target reservation date
        LocalDate appointmentDate = null;
        while (appointmentDate == null) {
            System.out.print("Enter Preferred Appointment Date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                appointmentDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid date format. Use exactly YYYY-MM-DD.");
            }
        }

        System.out.println("\n⏳ Validating schedule availability and saving records...");
        try {
            String generatedCode = patientController.handleBooking(
                firstName, middleName, lastName, dob, sex, address, 
                familyNumber, contactNumber, serviceId, appointmentDate
            );

            System.out.println("\n=============================================");
            System.out.println("🎉 APPOINTMENT BOOKED SUCCESSFULLY!");
            System.out.println("=============================================");
            System.out.println("Patient Name: " + firstName + " " + lastName);
            System.out.println("Tracking Code: " + generatedCode);
            System.out.println("---------------------------------------------");
            System.out.println("⚠️  IMPORTANT: Please take a screenshot or write");
            System.out.println("    down this code. Present it to the clinic desk");
            System.out.println("    on " + appointmentDate + ".");
            System.out.println("=============================================");

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Catches blank fields, past dates, or service-day schedule collisions gracefully
            System.out.println("\n❌ BOOKING FAILED");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}