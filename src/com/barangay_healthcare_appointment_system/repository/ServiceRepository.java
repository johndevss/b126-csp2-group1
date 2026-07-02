package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceRepository {

    public Service save(Service service) {
        String sql = "INSERT INTO services (name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, service.getName());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Service(generatedKeys.getInt(1), service.getName());
                }
            }

        } catch (SQLException e) {
            System.err.println("[ServiceRepository] Failed to save service: " + e.getMessage());
        }

        return null;
    }

    public Service findById(int id) {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Service(rs.getInt("id"), rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ServiceRepository] Failed to find service: " + e.getMessage());
        }

        return null;
    }

    public List<Service> findAll() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                services.add(new Service(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            System.err.println("[ServiceRepository] Failed to fetch services: " + e.getMessage());
        }

        return services;
    }
}