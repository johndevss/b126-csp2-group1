package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    // INSERT a new appointment.

    public Appointment save(Appointment appointment) {
        String sql = "INSERT INTO appointments " +
                "(appointment_code, patient_id, service_id, assigned_to, appointment_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, appointment.getAppointmentCode());
            stmt.setInt(2, appointment.getPatientId());
            stmt.setInt(3, appointment.getServiceId());


            if (appointment.getAssignedTo() != null) {
                stmt.setInt(4, appointment.getAssignedTo());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setDate(5, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setString(6, appointment.getStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Appointment(newId, appointment.getAppointmentCode(), appointment.getPatientId(),
                            appointment.getServiceId(), appointment.getAssignedTo(),
                            appointment.getAppointmentDate(), appointment.getStatus());
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to save appointment: " + e.getMessage());
        }

        return null;
    }

    public Appointment findById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to find appointment: " + e.getMessage());
        }

        return null;
    }

    public Appointment findByAppointmentCode(String code) {
        String sql = "SELECT * FROM appointments WHERE appointment_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to find appointment by code: " + e.getMessage());
        }

        return null;
    }

    public List<Appointment> findByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRowToAppointment(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to fetch appointments by date: " + e.getMessage());
        }

        return appointments;
    }

    public List<Appointment> findQueueForProvider(int providerId, LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments " +
                "WHERE assigned_to = ? AND appointment_date = ? AND status = 'arrived' " +
                "ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, providerId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRowToAppointment(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to fetch provider queue: " + e.getMessage());
        }

        return appointments;
    }

    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to update status: " + e.getMessage());
            return false;
        }
    }

    public boolean assignProvider(int appointmentId, int providerId) {
        String sql = "UPDATE appointments SET assigned_to = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, providerId);
            stmt.setInt(2, appointmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to assign provider: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRowToAppointment(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to fetch patient's appointments: " + e.getMessage());
        }

        return appointments;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[AppointmentRepository] Failed to count appointments: " + e.getMessage());
        }

        return 0;
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        LocalDate appointmentDate = rs.getDate("appointment_date").toLocalDate();

        int assignedToRaw = rs.getInt("assigned_to");
        Integer assignedTo = rs.wasNull() ? null : assignedToRaw;

        return new Appointment(
                rs.getInt("id"),
                rs.getString("appointment_code"),
                rs.getInt("patient_id"),
                rs.getInt("service_id"),
                assignedTo,
                appointmentDate,
                rs.getString("status")
        );
    }
}