USE cchc_clinic_system;

-- clean schema for cchc_clinic
CREATE DATABASE IF NOT EXISTS cchc_clinic_system;
USE cchc_clinic_system;

-- roles
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL UNIQUE,
    role_description VARCHAR(255)
);

-- users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- clinics
CREATE TABLE clinics (
    clinic_id INT AUTO_INCREMENT PRIMARY KEY,
    clinic_code VARCHAR(20) NOT NULL UNIQUE,
    clinic_name VARCHAR(120) NOT NULL,
    address_line1 VARCHAR(150),
    city VARCHAR(80),
    state VARCHAR(80),
    phone VARCHAR(25),
    opening_time TIME,
    closing_time TIME,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- services
CREATE TABLE services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_code VARCHAR(20) NOT NULL UNIQUE,
    service_name VARCHAR(120) NOT NULL,
    service_description VARCHAR(255),
    default_duration_minutes INT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- clinic_services
CREATE TABLE clinic_services (
    clinic_service_id INT AUTO_INCREMENT PRIMARY KEY,
    clinic_id INT NOT NULL,
    service_id INT NOT NULL,
    duration_minutes INT NOT NULL,
    fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_clinic_service (clinic_id, service_id),
    FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- patient_profiles
CREATE TABLE patient_profiles (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(25),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- staff_profiles
CREATE TABLE staff_profiles (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    clinic_id INT NOT NULL,
    employee_no VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(25),
    position_title VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id)
);

-- appointments (simple and correct)
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    clinic_id INT NOT NULL,
    service_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- queue_entries
CREATE TABLE queue_entries (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    clinic_id INT NOT NULL,
    service_id INT NOT NULL,
    patient_id INT NOT NULL,
    appointment_id INT NULL,
    queue_date DATE NOT NULL,
    token_no INT NOT NULL,
    priority_level TINYINT NOT NULL DEFAULT 0,
    queue_status VARCHAR(30) NOT NULL DEFAULT 'WAITING',
    called_time DATETIME NULL,
    service_start_time DATETIME NULL,
    service_end_time DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_daily_token (clinic_id, queue_date, token_no),
    FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    FOREIGN KEY (patient_id) REFERENCES patient_profiles(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- notifications
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_type VARCHAR(80) NOT NULL,
    related_appointment_id INT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (related_appointment_id) REFERENCES appointments(appointment_id)
);

-- system_settings
CREATE TABLE system_settings (
    setting_key VARCHAR(80) PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_by_user_id INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by_user_id) REFERENCES users(user_id)
);

-- csv_import_logs
CREATE TABLE csv_import_logs (
    import_id INT AUTO_INCREMENT PRIMARY KEY,
    import_type VARCHAR(30) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    total_rows INT NOT NULL,
    success_rows INT NOT NULL,
    failed_rows INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    imported_by_user_id INT NOT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (imported_by_user_id) REFERENCES users(user_id)
);

-- audit_logs
CREATE TABLE audit_logs (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(50),
    details VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
