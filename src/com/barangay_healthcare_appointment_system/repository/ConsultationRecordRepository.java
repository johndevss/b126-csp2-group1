package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.ConsultationRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRecordRepository {

    public ConsultationRecord save(ConsultationRecord record) {
        String sql = "INSERT INTO consultation_records (appointment_id, recorded_by, notes, consulted_at) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, record.getAppointmentId());
            stmt.setInt(2, record.getRecordedBy());
            stmt.setString(3, record.getNotes());
            stmt.setTimestamp(4, Timestamp.valueOf(record.getConsultedAt()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new ConsultationRecord(generatedKeys.getInt(1), record.getAppointmentId(),
                            record.getRecordedBy(), record.getNotes(), record.getConsultedAt());
                }
            }

        } catch (SQLException e) {
            System.err.println("[ConsultationRecordRepository] Failed to save record: " + e.getMessage());
        }

        return null;
    }

    public ConsultationRecord findByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM consultation_records WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToConsultationRecord(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[ConsultationRecordRepository] Failed to find record: " + e.getMessage());
        }

        return null; // null means "no consultation recorded yet for this appointment"
    }

    public List<ConsultationRecord> findByRecordedBy(int userId) {
        List<ConsultationRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM consultation_records WHERE recorded_by = ? ORDER BY consulted_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToConsultationRecord(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ConsultationRecordRepository] Failed to fetch records: " + e.getMessage());
        }

        return records;
    }

    private ConsultationRecord mapRowToConsultationRecord(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("consulted_at");
        LocalDateTime consultedAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new ConsultationRecord(
                rs.getInt("id"),
                rs.getInt("appointment_id"),
                rs.getInt("recorded_by"),
                rs.getString("notes"),
                consultedAt
        );
    }
}