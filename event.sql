-- XPerience Database Setup Script
-- Author: Mina Shehata
-- Assignment: XPerience Server DB Setup (Program 3)
-- Class: Software and System Security

-- Create database
CREATE DATABASE IF NOT EXISTS shehata;
USE shehata;

DROP TABLE IF EXISTS Event;

-- Create Event table with restrictive data types
CREATE TABLE IF NOT EXISTS Event (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(300) NOT NULL UNIQUE,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user with remote access
CREATE USER IF NOT EXISTS 'mina'@'%' IDENTIFIED BY 'password123';
GRANT SELECT, INSERT ON shehata.Event TO 'mina'@'%';

-- Enable remote access with mysql_native_password
ALTER USER 'mina'@'%' IDENTIFIED WITH mysql_native_password BY 'password123';

-- Apply privileges
FLUSH PRIVILEGES;
