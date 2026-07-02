package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.Patient;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    // INSERT a new patient into the DB.
    public Patient save(Patient patient) {
        String sql = "INSERT INTO patients " +
                "(first_name, middle_name, last_name, date_of_birth, sex, address, family_number, contact_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(5, patient.getSex());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getFamilyNumber());
            stmt.setString(8, patient.getContactNumber());

            stmt.executeUpdate();

            // Grab the auto-generated `id` the DB just assigned
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Patient(newId, patient.getFirstName(), patient.getMiddleName(),
                            patient.getLastName(), patient.getDateOfBirth(), patient.getSex(),
                            patient.getAddress(), patient.getFamilyNumber(), patient.getContactNumber(),
                            LocalDateTime.now());
                }
            }

        } catch (SQLException e) {
            System.err.println("[PatientRepository] Failed to save patient: " + e.getMessage());
        }

        return null;
    }

    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[PatientRepository] Failed to find patient: " + e.getMessage());
        }

        return null;
    }

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY last_name, first_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRowToPatient(rs));
            }

        } catch (SQLException e) {
            System.err.println("[PatientRepository] Failed to fetch patients: " + e.getMessage());
        }

        return patients;
    }

    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, middle_name = ?, last_name = ?, " +
                "date_of_birth = ?, sex = ?, address = ?, family_number = ?, contact_number = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(5, patient.getSex());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getFamilyNumber());
            stmt.setString(8, patient.getContactNumber());
            stmt.setInt(9, patient.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[PatientRepository] Failed to update patient: " + e.getMessage());
            return false;
        }
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        LocalDate dob = rs.getDate("date_of_birth").toLocalDate();

        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTimestamp != null) ? createdAtTimestamp.toLocalDateTime() : null;

        return new Patient(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                dob,
                rs.getString("sex"),
                rs.getString("address"),
                rs.getString("family_number"),
                rs.getString("contact_number"),
                createdAt
        );
    }
}