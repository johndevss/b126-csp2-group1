package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.ServiceSchedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all SQL for the `service_schedules` table.
 *
 * This table answers ONE question: "which days does this service run?"
 * It's what will later enforce your rule "Tuesday = Vaccination only" —
 * think of it like a cron schedule table: each row says "this service
 * runs on this day," and AppointmentRepository will check against these
 * rows before allowing a booking, same as how a CI pipeline checks a
 * schedule config before deciding whether a scheduled job should fire.
 */
public class ServiceScheduleRepository {

    public ServiceSchedule save(ServiceSchedule schedule) {
        String sql = "INSERT INTO service_schedules (service_id, day_of_week) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, schedule.getServiceId());
            stmt.setString(2, schedule.getDayOfWeek());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new ServiceSchedule(generatedKeys.getInt(1),
                            schedule.getServiceId(), schedule.getDayOfWeek());
                }
            }

        } catch (SQLException e) {
            System.err.println("[ServiceScheduleRepository] Failed to save schedule: " + e.getMessage());
        }

        return null;
    }

    /**
     * All schedule rows for ONE service.
     * e.g. findByServiceId(1) -> [monday, friday]  (General Checkup)
     *
     * Useful for showing a patient "this service is available on: Mon, Fri"
     * during booking.
     */
    public List<ServiceSchedule> findByServiceId(int serviceId) {
        List<ServiceSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM service_schedules WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapRowToServiceSchedule(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ServiceScheduleRepository] Failed to fetch schedules: " + e.getMessage());
        }

        return schedules;
    }

    /**
     * THE important one for your booking rule enforcement.
     * "Is this service actually offered on this day of the week?"
     *
     * AppointmentService will call this BEFORE letting a patient book —
     * e.g. isServiceAvailableOnDay(serviceId=2, "tuesday") -> true (Vaccination)
     *      isServiceAvailableOnDay(serviceId=2, "monday")  -> false
     *
     * Doing this check as a targeted query (COUNT) instead of pulling
     * ALL schedules and looping in Java is the same instinct as filtering
     * with `kubectl get pods --field-selector` on the server side instead
     * of pulling every pod and filtering client-side — let the database
     * do the filtering, it's built for that.
     */
    public boolean isServiceAvailableOnDay(int serviceId, String dayOfWeek) {
        String sql = "SELECT COUNT(*) FROM service_schedules WHERE service_id = ? AND day_of_week = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceId);
            stmt.setString(2, dayOfWeek.toLowerCase()); // normalize case, in case caller passes "Monday" vs "monday"

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("[ServiceScheduleRepository] Failed to check availability: " + e.getMessage());
        }

        return false; // fail-safe: if something goes wrong, assume NOT available rather than letting a bad booking through
    }

    public List<ServiceSchedule> findAll() {
        List<ServiceSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM service_schedules";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                schedules.add(mapRowToServiceSchedule(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ServiceScheduleRepository] Failed to fetch all schedules: " + e.getMessage());
        }

        return schedules;
    }

    private ServiceSchedule mapRowToServiceSchedule(ResultSet rs) throws SQLException {
        return new ServiceSchedule(
                rs.getInt("id"),
                rs.getInt("service_id"),
                rs.getString("day_of_week")
        );
    }
}