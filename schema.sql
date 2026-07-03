-- Barangay Healthcare Appointment System — Database Schema
-- Run this once to create all 6 tables.
--
-- Usage:
--   mysql -u root -p < schema.sql
--

CREATE DATABASE IF NOT EXISTS barangay_healthcare_db;
USE barangay_healthcare_db;

-- 1. patients
CREATE TABLE IF NOT EXISTS patients (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(255) NOT NULL,
    middle_name     VARCHAR(255),
    last_name       VARCHAR(255) NOT NULL,
    date_of_birth   DATE NOT NULL,
    sex             ENUM('male', 'female') NOT NULL,
    address         VARCHAR(255) NOT NULL,
    family_number   VARCHAR(255),
    contact_number  VARCHAR(11) NOT NULL
);

-- 2. users (staff / healthcare providers)
CREATE TABLE IF NOT EXISTS users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(255) NOT NULL,
    middle_name     VARCHAR(255),
    last_name       VARCHAR(255) NOT NULL,
    username        VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL  -- admin, nurse, doctor, midwife, nutritionist
);

-- 3. services
CREATE TABLE IF NOT EXISTS services (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(255) NOT NULL
);

-- 4. service_schedules
CREATE TABLE IF NOT EXISTS service_schedules (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    service_id      INT NOT NULL,
    day_of_week     VARCHAR(255) NOT NULL,  -- monday, tuesday, wednesday, friday
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- 5. appointments
CREATE TABLE IF NOT EXISTS appointments (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    appointment_code    VARCHAR(255) NOT NULL UNIQUE,
    patient_id          INT NOT NULL,
    service_id          INT NOT NULL,
    assigned_to         INT,
    appointment_date    DATE NOT NULL,
    status              VARCHAR(15) NOT NULL DEFAULT 'pending', -- pending, arrived, consulted, cancelled
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
);

-- 6. consultation_records
CREATE TABLE IF NOT EXISTS consultation_records (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id  INT NOT NULL,
    recorded_by     INT NOT NULL,
    notes           TEXT,
    consulted_at    DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (recorded_by) REFERENCES users(id)
);

-- 7. Seed data: Users with BCrypt encrypted passwords
INSERT INTO users (first_name, middle_name, last_name, username, password, role) VALUES
    ('Raymond', NULL, 'Tobias', 'raymond_admin', '$2a$10$l3KsHG/Toj.e/xxHO589HuDQyM7jaA7o77sbuPTXvH1abr.6U6.Eu', 'admin'),
    ('Ashley Eunice', NULL, 'Cruz', 'ashley_nurse', '$2a$10$g0UTpiKnw.3DPMqUNTxp9OR6shGZft4tJQq/CO6xyC0Jl4.YZYSoW', 'staff'),
    ('Fredper', NULL, 'Sentes', 'fredper_doc', '$2a$10$dwZzpm.ba6MkdaYLAtZcN.pm12rwxDaiZv.CRs0UU1K1YTZNKXeQe', 'doctor'),
    ('John Emmanuel', NULL, 'Calderon', 'john_doc', '$2a$10$dwZzpm.ba6MkdaYLAtZcN.pm12rwxDaiZv.CRs0UU1K1YTZNKXeQe', 'doctor'),
    ('Bea', NULL, 'Gorospe', 'bea_midwife', '$2a$10$O/wLQSreC4ROenMKK9tJw.naEXXeabKrp2k5ruCn5BpU9m2i.rXsC', 'midwife');
    
-- Seed data: services + their schedule days (from the project doc)
INSERT INTO services (name) VALUES
    ('General Checkup'),
    ('Baby Vaccination'),
    ('Prenatal (Buntis Check-up)');

INSERT INTO service_schedules (service_id, day_of_week) VALUES
    (1, 'monday'),
    (1, 'friday'),
    (2, 'tuesday'),
    (3, 'wednesday');