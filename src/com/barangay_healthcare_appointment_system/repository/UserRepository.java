package com.barangay_healthcare_appointment_system.repository;

import com.barangay_healthcare_appointment_system.config.DBConnection;
import com.barangay_healthcare_appointment_system.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all SQL for the `users` table (staff/providers who log in).
 */
public class UserRepository {

    public User save(User user) {
        String sql = "INSERT INTO users (first_name, middle_name, last_name, username, password, role) " +
                    "VALUES (?, ?, ?, ?, SHA2(?, 256), ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getMiddleName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getUsername());
            stmt.setString(5, user.getPassword()); // Pass the plain text; MySQL hashes it!
            stmt.setString(6, user.getRole());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new User(generatedKeys.getInt(1), user.getFirstName(), user.getMiddleName(),
                            user.getLastName(), user.getUsername(), user.getPassword(), user.getRole());
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to save user: " + e.getMessage());
        }

        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to find user by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET first_name = ?, middle_name = ?, last_name = ?, " +
                    "password = ?, role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getMiddleName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getPassword()); // Accepts the BCrypt hash string directly
            stmt.setString(5, user.getRole());
            stmt.setInt(6, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserRepository] Target row failed to update: " + e.getMessage());
            return false;
        }
    }


    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to find user by username: " + e.getMessage());
        }

        return null; 
    }

    // Finds the doctor with the fewest active appointments on a given date.
    public User findLeastBusyProvider(LocalDate date) {
        String sql = "SELECT u.* FROM users u " +
                "LEFT JOIN appointments a " +
                "  ON a.assigned_to = u.id " +
                "  AND a.appointment_date = ? " +
                "  AND a.status IN ('pending', 'arrived') " +
                "WHERE u.role = 'doctor' " +
                "GROUP BY u.id " +
                "ORDER BY COUNT(a.id) ASC " +
                "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to find least busy provider: " + e.getMessage());
        }

        return null; // no doctors registered — nobody to assign
    }

    public User findByCredentials(String username, String password) {
    // MySQL hashes the incoming plain-text password attempt and compares it to the DB hash
        String sql = "SELECT * FROM users WHERE username = ? AND password = SHA2(?, 256)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Authentication query failed: " + e.getMessage());
        }

        return null; // Returns null if username doesn't exist OR password doesn't match
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY last_name, first_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to fetch users: " + e.getMessage());
        }

        return users;
    }

    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY last_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Failed to fetch users by role: " + e.getMessage());
        }

        return users;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}