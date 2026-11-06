-- Prison Management System Database Schema & Seed Data

CREATE DATABASE IF NOT EXISTS prison_management;
USE prison_management;

-- 1. Cells Table
CREATE TABLE IF NOT EXISTS cells (
    id INT AUTO_INCREMENT PRIMARY KEY,
    block_name VARCHAR(50) NOT NULL,
    cell_number VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    current_occupancy INT DEFAULT 0,
    security_level VARCHAR(50) NOT NULL
);

-- 2. Prisoners Table
CREATE TABLE IF NOT EXISTS prisoners (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    crime_type VARCHAR(100) NOT NULL,
    sentence_start DATE NOT NULL,
    sentence_end DATE NOT NULL,
    cell_id INT,
    health_status VARCHAR(50) DEFAULT 'Good',
    behavior_status VARCHAR(50) DEFAULT 'Good',
    FOREIGN KEY (cell_id) REFERENCES cells(id) ON DELETE SET NULL
);

-- 3. Legal Events Table
CREATE TABLE IF NOT EXISTS legal_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prisoner_id INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    description TEXT,
    FOREIGN KEY (prisoner_id) REFERENCES prisoners(id) ON DELETE CASCADE
);

-- 4. Staff Table
CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL
);

-- ----------------------------------------------------
-- Seed Data
-- ----------------------------------------------------

-- Seed Cells
INSERT INTO cells (id, block_name, cell_number, capacity, current_occupancy, security_level) VALUES
(1, 'Block A', '101', 2, 2, 'Minimum'),
(2, 'Block A', '102', 2, 1, 'Minimum'),
(3, 'Block A', '103', 4, 0, 'Minimum'),
(4, 'Block B', '201', 2, 1, 'Medium'),
(5, 'Block B', '202', 2, 1, 'Medium'),
(6, 'Block B', '203', 3, 0, 'Medium'),
(7, 'Block C', '301', 1, 1, 'Maximum'),
(8, 'Block C', '302', 1, 0, 'Maximum')
ON DUPLICATE KEY UPDATE id=id;

-- Seed Prisoners
INSERT INTO prisoners (id, first_name, last_name, dob, gender, crime_type, sentence_start, sentence_end, cell_id, health_status, behavior_status) VALUES
(1, 'John', 'Miller', '1988-04-12', 'Male', 'Grand Theft', '2023-01-15', '2026-01-15', 1, 'Good', 'Good'),
(2, 'Robert', 'Davis', '1992-09-23', 'Male', 'Identity Fraud', '2023-05-10', '2025-11-10', 1, 'Fair', 'Normal'),
(3, 'Michael', 'Johnson', '1985-11-04', 'Male', 'Tax Evasion', '2022-03-01', '2025-09-01', 2, 'Good', 'Good'),
(4, 'James', 'Smith', '1990-07-19', 'Male', 'Armed Robbery', '2021-08-14', '2031-08-14', 4, 'Fair', 'Problematic'),
(5, 'David', 'Wilson', '1987-12-30', 'Male', 'Drug Trafficking', '2022-06-20', '2027-06-20', 5, 'Poor', 'Normal'),
(6, 'Marcus', 'Stone', '1983-02-14', 'Male', 'Aggravated Assault and Battery', '2020-02-10', '2035-02-10', 7, 'Good', 'Violent')
ON DUPLICATE KEY UPDATE id=id;

-- Seed Legal Events
INSERT INTO legal_events (id, prisoner_id, event_type, event_date, description) VALUES
(1, 1, 'Arraignment', '2023-01-20', 'Initial court arraignment and plea entry.'),
(2, 1, 'Parole Hearing', '2024-06-15', 'First annual parole evaluation. Parole denied due to restitution pending.'),
(3, 4, 'Appeal Hearing', '2023-11-10', 'Sentence appeal hearing before the state appellate division.'),
(4, 5, 'Medical Review', '2024-02-28', 'Quarterly medical examination and prescription review.'),
(5, 6, 'Disciplinary Hearing', '2024-04-05', 'Incident review regarding altercation in exercise yard.')
ON DUPLICATE KEY UPDATE id=id;

-- Seed Staff
-- Password for admin: admin123
INSERT INTO staff (id, first_name, last_name, position, department, username, password_hash, salt) VALUES
(1, 'System', 'Admin', 'Administrator', 'Management', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin_salt_1234')
ON DUPLICATE KEY UPDATE id=id;
