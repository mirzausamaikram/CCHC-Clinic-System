USE cchc_clinic_system;

-- Roles
INSERT INTO roles (role_name, role_description) VALUES
('PATIENT', 'Clinic patient'),
('STAFF', 'Clinic front desk / nurse'),
('ADMIN', 'System administrator');

-- Clinics (5)
INSERT INTO clinics
(clinic_code, clinic_name, address_line1, address_line2, city, state, postal_code, phone, email, opening_time, closing_time, is_active)
VALUES
('CLN-001', 'CCHC Central Clinic', '12 Jalan Sultan', 'Level 2', 'Kuala Lumpur', 'WP Kuala Lumpur', '50000', '03-22001111', 'central@cchc.my', '08:00:00', '17:00:00', 1),
('CLN-002', 'CCHC North Clinic', '88 Persiaran Utara', NULL, 'Shah Alam', 'Selangor', '40100', '03-55112222', 'north@cchc.my', '08:30:00', '17:30:00', 1),
('CLN-003', 'CCHC South Clinic', '21 Jalan Selatan', NULL, 'Johor Bahru', 'Johor', '80000', '07-33113333', 'south@cchc.my', '08:00:00', '17:00:00', 1),
('CLN-004', 'CCHC East Clinic', '5 Lorong Timur', 'Block B', 'Kuantan', 'Pahang', '25000', '09-55114444', 'east@cchc.my', '08:00:00', '16:30:00', 1),
('CLN-005', 'CCHC West Clinic', '101 Jalan Barat', NULL, 'Ipoh', 'Perak', '30000', '05-24115555', 'west@cchc.my', '08:30:00', '17:00:00', 1);

-- Services (10)
INSERT INTO services
(service_code, service_name, service_description, default_duration_minutes, is_active)
VALUES
('SRV-001', 'General Consultation', 'Basic doctor consultation', 20, 1),
('SRV-002', 'Follow-up Consultation', 'Post-treatment follow-up', 15, 1),
('SRV-003', 'Vaccination', 'Adult/child immunization', 15, 1),
('SRV-004', 'Blood Test', 'Routine blood panel', 10, 1),
('SRV-005', 'Health Screening', 'Annual health check', 30, 1),
('SRV-006', 'Chronic Disease Review', 'Diabetes/Hypertension monitoring', 25, 1),
('SRV-007', 'Wound Dressing', 'Wound care and dressing', 20, 1),
('SRV-008', 'Antenatal Checkup', 'Prenatal routine check', 25, 1),
('SRV-009', 'Pediatric Consultation', 'Children consultation', 20, 1),
('SRV-010', 'Physiotherapy Session', 'Basic physio treatment', 30, 1);

-- Clinic service offerings
INSERT INTO clinic_services (clinic_id, service_id, duration_minutes, fee, is_active) VALUES
(1,1,20,30.00,1),(1,2,15,25.00,1),(1,3,15,20.00,1),(1,4,10,18.00,1),(1,5,30,80.00,1),
(2,1,20,30.00,1),(2,6,25,40.00,1),(2,7,20,22.00,1),(2,9,20,35.00,1),
(3,1,20,28.00,1),(3,2,15,24.00,1),(3,8,25,45.00,1),(3,9,20,34.00,1),
(4,1,20,29.00,1),(4,3,15,20.00,1),(4,4,10,17.00,1),(4,10,30,50.00,1),
(5,1,20,30.00,1),(5,5,30,78.00,1),(5,6,25,39.00,1),(5,10,30,48.00,1);

-- Users (admins, staff, patients)
-- Temporary demo passwords are stored in password_hash for local testing.
-- Replace with real hashed passwords before final submission.
INSERT INTO users (role_id, username, email, password_hash, is_active) VALUES
(3, 'admin1', 'admin1@cchc.my', 'admin123', 1),
(3, 'admin2', 'admin2@cchc.my', 'admin123', 1),
(2, 'staff1',   'amy.staff@cchc.my',   'staff123', 1),
(2, 'staff2',   'ben.staff@cchc.my',   'staff123', 1),
(2, 'staff_chong', 'chong.staff@cchc.my', 'staff123', 1),
(2, 'staff_dina',  'dina.staff@cchc.my',  'staff123', 1),
(1, 'patient1',   'ali.patient@gmail.com',   'patient123', 1),
(1, 'patient2',  'bala.patient@gmail.com',  'patient123', 1),
(1, 'patient_cindy', 'cindy.patient@gmail.com', 'patient123', 1),
(1, 'patient_devi',  'devi.patient@gmail.com',  'patient123', 1),
(1, 'patient_eddie', 'eddie.patient@gmail.com', 'patient123', 1);

-- Staff profiles
INSERT INTO staff_profiles (user_id, clinic_id, employee_no, full_name, phone, position_title) VALUES
((SELECT user_id FROM users WHERE username='staff_amy'),   1, 'EMP-1001', 'Amy Tan', '012-3100001', 'Front Desk Staff'),
((SELECT user_id FROM users WHERE username='staff_ben'),   2, 'EMP-1002', 'Ben Lim', '012-3100002', 'Nurse'),
((SELECT user_id FROM users WHERE username='staff_chong'), 3, 'EMP-1003', 'Chong Wei', '012-3100003', 'Front Desk Staff'),
((SELECT user_id FROM users WHERE username='staff_dina'),  4, 'EMP-1004', 'Dina Yusuf', '012-3100004', 'Nurse');

-- Patient profiles
INSERT INTO patient_profiles
(user_id, ic_passport_no, full_name, date_of_birth, gender, phone, address, emergency_contact_name, emergency_contact_phone) VALUES
((SELECT user_id FROM users WHERE username='patient_ali'),   '900101-10-1111', 'Ali Rahman',   '1990-01-01', 'MALE',   '013-4001001', 'KL City',        'Aisha Rahman', '013-5001001'),
((SELECT user_id FROM users WHERE username='patient_bala'),  '880202-08-2222', 'Bala Kumar',   '1988-02-02', 'MALE',   '013-4001002', 'Shah Alam',      'Mira Kumar',   '013-5001002'),
((SELECT user_id FROM users WHERE username='patient_cindy'), '950303-14-3333', 'Cindy Wong',   '1995-03-03', 'FEMALE', '013-4001003', 'Johor Bahru',    'Jason Wong',   '013-5001003'),
((SELECT user_id FROM users WHERE username='patient_devi'),  '920404-07-4444', 'Devi Nair',    '1992-04-04', 'FEMALE', '013-4001004', 'Kuantan',        'Ravi Nair',    '013-5001004'),
((SELECT user_id FROM users WHERE username='patient_eddie'), '970505-12-5555', 'Eddie Chua',   '1997-05-05', 'MALE',   '013-4001005', 'Ipoh',           'May Chua',     '013-5001005');

-- Appointments (sample)
INSERT INTO appointments
(patient_id, clinic_service_id, assigned_staff_id, appointment_date, start_time, end_time, booking_channel, status, notes, created_by_user_id)
VALUES
(
 (SELECT patient_id FROM patient_profiles pp JOIN users u ON pp.user_id=u.user_id WHERE u.username='patient_ali'),
 (SELECT clinic_service_id FROM clinic_services WHERE clinic_id=1 AND service_id=1),
 (SELECT staff_id FROM staff_profiles sp JOIN users u ON sp.user_id=u.user_id WHERE u.username='staff_amy'),
 '2026-03-20', '09:00:00', '09:20:00', 'ONLINE', 'BOOKED', 'First visit',
 (SELECT user_id FROM users WHERE username='patient_ali')
),
(
 (SELECT patient_id FROM patient_profiles pp JOIN users u ON pp.user_id=u.user_id WHERE u.username='patient_bala'),
 (SELECT clinic_service_id FROM clinic_services WHERE clinic_id=2 AND service_id=6),
 (SELECT staff_id FROM staff_profiles sp JOIN users u ON sp.user_id=u.user_id WHERE u.username='staff_ben'),
 '2026-03-20', '10:00:00', '10:25:00', 'STAFF_DESK', 'CONFIRMED', 'Hypertension review',
 (SELECT user_id FROM users WHERE username='staff_ben')
);

-- Queue entries (sample)
INSERT INTO queue_entries
(clinic_id, service_id, patient_id, appointment_id, queue_date, token_no, priority_level, queue_status)
VALUES
(
 1, 1,
 (SELECT patient_id FROM patient_profiles pp JOIN users u ON pp.user_id=u.user_id WHERE u.username='patient_ali'),
 (SELECT appointment_id FROM appointments ORDER BY appointment_id ASC LIMIT 1),
 '2026-03-20', 1, 0, 'WAITING'
),
(
 2, 6,
 (SELECT patient_id FROM patient_profiles pp JOIN users u ON pp.user_id=u.user_id WHERE u.username='patient_bala'),
 (SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1),
 '2026-03-20', 1, 1, 'WAITING'
),
(
 1, 3,
 (SELECT patient_id FROM patient_profiles pp JOIN users u ON pp.user_id=u.user_id WHERE u.username='patient_cindy'),
 NULL,
 '2026-03-20', 2, 0, 'WAITING'
);

-- Notifications (sample)
INSERT INTO notifications (user_id, title, message, notification_type, related_appointment_id, is_read) VALUES
((SELECT user_id FROM users WHERE username='patient_ali'), 'Appointment Booked', 'Your appointment on 2026-03-20 at 09:00 is booked.', 'APPOINTMENT', (SELECT appointment_id FROM appointments ORDER BY appointment_id ASC LIMIT 1), 0),
((SELECT user_id FROM users WHERE username='patient_bala'), 'Appointment Confirmed', 'Your chronic disease review is confirmed.', 'APPOINTMENT', (SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1), 0),
((SELECT user_id FROM users WHERE username='staff_amy'), 'Queue Started', 'Queue for CCHC Central Clinic has started.', 'QUEUE', NULL, 0);

-- System settings (extra feature)
INSERT INTO system_settings (setting_key, setting_value, description, updated_by_user_id) VALUES
('MAX_DAILY_APPOINTMENTS_PER_PATIENT', '3', 'Maximum appointments a patient can book per day', (SELECT user_id FROM users WHERE username='admin1')),
('QUEUE_TOKEN_PREFIX', 'CCHC', 'Prefix used for queue token display', (SELECT user_id FROM users WHERE username='admin1')),
('APPOINTMENT_REMINDER_HOURS', '24', 'Hours before appointment to send reminder', (SELECT user_id FROM users WHERE username='admin2'));

-- CSV import logs (extra feature)
INSERT INTO csv_import_logs (import_type, file_name, total_rows, success_rows, failed_rows, status, imported_by_user_id) VALUES
('PATIENT', 'patients_batch_202603.csv', 120, 118, 2, 'PARTIAL', (SELECT user_id FROM users WHERE username='admin1')),
('SERVICE', 'service_update_202603.csv', 10, 10, 0, 'SUCCESS', (SELECT user_id FROM users WHERE username='admin2'));

-- Audit logs (sample)
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details) VALUES
((SELECT user_id FROM users WHERE username='admin1'), 'LOGIN', 'USER', NULL, 'Admin login success'),
((SELECT user_id FROM users WHERE username='patient_ali'), 'BOOK_APPOINTMENT', 'APPOINTMENT', '1', 'Booked via online portal'),
((SELECT user_id FROM users WHERE username='staff_amy'), 'UPDATE_QUEUE', 'QUEUE', '1', 'Called token 1');
