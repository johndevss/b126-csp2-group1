package com.barangay_healthcare_appointment_system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static final String HOST = EnvLoader.get("DB_HOST", "127.0.0.1");
    private static final String PORT = EnvLoader.get("DB_PORT", "3306");
    private static final String DB_NAME = EnvLoader.get("DB_NAME", "barangay_healthcare");
    private static final String USER = EnvLoader.get("DB_USER", "root");
    private static final String PASSWORD = EnvLoader.get("DB_PASSWORD", "");

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
                    + "?useSSL=false&serverTimezone=Asia/Manila&allowPublicKeyRetrieval=true";

    public static Connection getConnection() throws SQLException {
        try {
            // Loads the MySQL JDBC driver into memory.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                    + "Make sure mysql-connector-j .jar is added to your classpath.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connected successfully to: " + URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}