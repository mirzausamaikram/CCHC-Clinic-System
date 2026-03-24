USE cchc_clinic_system;

-- ============================================================
-- CCHC Clinic System - Comprehensive Sample Data
-- Ordered inserts to satisfy all foreign keys
-- ============================================================

-- Clean old data so this file can be re-run safely
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM audit_logs;
DELETE FROM csv_import_logs;
DELETE FROM notifications;
DELETE FROM queue_entries;
DELETE FROM appointments;
DELETE FROM system_settings;
DELETE FROM staff_profiles;
DELETE FROM patient_profiles;
DELETE FROM clinic_services;
DELETE FROM users;
DELETE FROM services;
DELETE FROM clinics;
DELETE FROM roles;

ALTER TABLE roles AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE clinics AUTO_INCREMENT = 1;
ALTER TABLE services AUTO_INCREMENT = 1;
ALTER TABLE clinic_services AUTO_INCREMENT = 1;
ALTER TABLE patient_profiles AUTO_INCREMENT = 1;
ALTER TABLE staff_profiles AUTO_INCREMENT = 1;
ALTER TABLE appointments AUTO_INCREMENT = 1;
ALTER TABLE queue_entries AUTO_INCREMENT = 1;
ALTER TABLE notifications AUTO_INCREMENT = 1;
ALTER TABLE csv_import_logs AUTO_INCREMENT = 1;
ALTER TABLE audit_logs AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- Roles first (required by users.role_id)
INSERT INTO roles (role_id, role_name, role_description) VALUES
(1, 'PATIENT', 'Clinic patient who books appointments'),
(2, 'STAFF',   'Clinic staff who manages appointments and queues'),
(3, 'ADMIN',   'System administrator with full access');

-- Clinics
INSERT INTO clinics (clinic_id, clinic_code, clinic_name, address_line1, city, state, phone, opening_time, closing_time, is_active) VALUES
(1, 'CL001', 'CCHC Chai Wan',      '28 Hau Fook Street',        'Chai Wan',      'Hong Kong',         '2568-0001', '08:00:00', '18:00:00', 1),
(2, 'CL002', 'CCHC Tseung Kwan O', '10 Lohas Park Boulevard',   'Tseung Kwan O', 'New Territories',   '2187-0002', '08:00:00', '18:00:00', 1),
(3, 'CL003', 'CCHC Sha Tin',       '100 Lung Wai Street',       'Sha Tin',       'New Territories',   '2601-0003', '08:00:00', '18:00:00', 1),
(4, 'CL004', 'CCHC Tuen Mun',      '50 Tuen Mun Heung Sze Wai', 'Tuen Mun',      'New Territories',   '2456-0004', '08:00:00', '18:00:00', 1),
(5, 'CL005', 'CCHC Tsing Yi',      '30 Ching Cheung Road',      'Tsing Yi',      'New Territories',   '2431-0005', '08:00:00', '18:00:00', 1);

-- Services
INSERT INTO services (service_id, service_code, service_name, service_description, default_duration_minutes, is_active) VALUES
(1,  'SRV001', 'General Consultation',         'General medical consultation and diagnosis',      20, 1),
(2,  'SRV002', 'Dental Check-up',              'Dental examination and professional cleaning',    30, 1),
(3,  'SRV003', 'Eye Care',                     'Comprehensive eye examination and vision testing',25, 1),
(4,  'SRV004', 'Traditional Chinese Medicine', 'TCM consultation and treatment',                  30, 1),
(5,  'SRV005', 'Health Screening',             'Full body health screening package',              45, 1),
(6,  'SRV006', 'Vaccination',                  'Vaccinations and immunization services',          15, 1),
(7,  'SRV007', 'Blood Pressure Check',         'Hypertension monitoring and counseling',          10, 1),
(8,  'SRV008', 'Wound Care',                   'Wound dressing and infection prevention',         20, 1),
(9,  'SRV009', 'Chronic Disease Management',   'Diabetes and chronic condition support',          25, 1),
(10, 'SRV010', 'Mental Health Counseling',     'Psychological support and counseling',            30, 1);

-- Clinic service mapping
INSERT INTO clinic_services (clinic_id, service_id, duration_minutes, fee, is_active) VALUES
(1,1,20,100.00,1),(1,2,30,200.00,1),(1,3,25,150.00,1),(1,5,45,280.00,1),(1,6,15,50.00,1),(1,7,10,30.00,1),
(2,1,20,95.00,1),(2,4,30,180.00,1),(2,5,45,280.00,1),(2,6,15,50.00,1),(2,9,25,120.00,1),
(3,1,20,90.00,1),(3,2,30,190.00,1),(3,3,25,140.00,1),(3,4,30,170.00,1),(3,8,20,80.00,1),
(4,1,20,100.00,1),(4,4,30,180.00,1),(4,5,45,300.00,1),(4,6,15,50.00,1),(4,10,30,150.00,1),
(5,1,20,110.00,1),(5,3,25,160.00,1),(5,5,45,320.00,1),(5,7,10,35.00,1),(5,9,25,130.00,1);

-- Users (IDs fixed so dependent rows are stable)
INSERT INTO users (user_id, role_id, username, email, full_name, phone, password_hash, is_active) VALUES
(1,3,'admin1','admin1@cchc.hk','Dr. Sarah Wong','2568-1001','password',1),
(2,3,'admin2','admin2@cchc.hk','Mr. James Leung','2568-1002','password',1),
(3,2,'staff1','staff1@cchc.hk','Nurse Emily Lam','2568-2001','password',1),
(4,2,'staff2','staff2@cchc.hk','Receptionist Tony Ho','2187-2001','password',1),
(5,2,'staff3','staff3@cchc.hk','Clinic Assistant May','2601-2001','password',1),
(6,1,'patient1','patient1@cchc.hk','Wong Ming Ho','9876-0001','password',1),
(7,1,'patient2','patient2@cchc.hk','Lee Sue Lin','9876-0002','password',1),
(8,1,'patient3','patient3@cchc.hk','Chan Yuk Ming','9876-0003','password',1),
(9,1,'patient4','patient4@cchc.hk','Tang Chi Yee','9876-0004','password',1),
(10,1,'patient5','patient5@cchc.hk','Cheung Wai Kin','9876-0005','password',1);

-- Staff profiles
INSERT INTO staff_profiles (user_id, clinic_id, employee_no, full_name, phone, position_title) VALUES
(3,1,'EMP-001','Nurse Emily Lam','2568-2001','Registered Nurse'),
(4,2,'EMP-002','Receptionist Tony Ho','2187-2001','Front Desk Officer'),
(5,3,'EMP-003','Clinic Assistant May','2601-2001','Clinic Assistant');

-- Patient profiles (patient_id will be 1..5)
INSERT INTO patient_profiles (user_id, full_name, phone) VALUES
(6,'Wong Ming Ho','9876-0001'),
(7,'Lee Sue Lin','9876-0002'),
(8,'Chan Yuk Ming','9876-0003'),
(9,'Tang Chi Yee','9876-0004'),
(10,'Cheung Wai Kin','9876-0005');

-- System settings
INSERT INTO system_settings (setting_key, setting_value, description, updated_by_user_id) VALUES
('maxBookingsPerPatient','3','Max active bookings per patient',1),
('cancellationCutoffHours','24','Cancellation cutoff in hours',1),
('MAX_DAILY_APPOINTMENTS_PER_PATIENT','3','Legacy key for max daily appointments',1),
('queueEnabled','1','Global queue enable flag',1),
('queueEnabled_clinic_1','1','Queue enabled for clinic 1',1),
('queueEnabled_clinic_2','1','Queue enabled for clinic 2',1),
('queueEnabled_clinic_3','1','Queue enabled for clinic 3',1),
('queueEnabled_clinic_4','1','Queue enabled for clinic 4',1),
('queueEnabled_clinic_5','1','Queue enabled for clinic 5',1);

-- Appointments (41 records across Mar-Apr 2026)
INSERT INTO appointments (user_id, clinic_id, service_id, appointment_date, time_slot, status, notes) VALUES
(6,1,1,'2026-03-02','09:00-09:20','COMPLETED','General checkup'),
(6,1,2,'2026-03-04','10:00-10:30','COMPLETED','Dental cleaning'),
(6,2,4,'2026-03-09','14:00-14:30','COMPLETED','TCM first visit'),
(6,3,3,'2026-03-11','11:00-11:25','COMPLETED','Eye test'),
(6,1,6,'2026-03-16','15:00-15:15','COMPLETED','Vaccination'),
(6,4,5,'2026-03-18','09:30-10:15','NO_SHOW','Health screening no-show'),
(6,5,7,'2026-03-23','13:00-13:10','COMPLETED','Blood pressure check'),
(7,2,1,'2026-03-03','09:30-09:50','COMPLETED','Flu symptoms'),
(7,3,2,'2026-03-05','13:00-13:30','COMPLETED','Dental exam'),
(7,4,5,'2026-03-10','15:00-15:45','COMPLETED','Annual screening'),
(7,1,3,'2026-03-12','10:30-10:55','COMPLETED','Eye prescription'),
(7,2,9,'2026-03-17','11:00-11:25','COMPLETED','Diabetes control'),
(7,5,4,'2026-03-20','14:30-15:00','CANCELLED','Patient cancelled'),
(7,3,8,'2026-03-25','10:00-10:20','COMPLETED','Wound care check'),
(8,3,1,'2026-03-01','08:30-08:50','COMPLETED','General consultation'),
(8,5,5,'2026-03-06','09:00-09:45','COMPLETED','Health package'),
(8,2,4,'2026-03-08','14:30-15:00','COMPLETED','TCM knee pain'),
(8,4,1,'2026-03-13','16:00-16:20','COMPLETED','Follow-up visit'),
(8,1,7,'2026-03-15','09:00-09:10','NO_SHOW','Blood pressure no-show'),
(8,3,10,'2026-03-19','11:00-11:30','COMPLETED','Mental health session'),
(8,5,3,'2026-03-22','15:30-15:55','COMPLETED','Eye follow-up'),
(9,4,4,'2026-03-07','10:00-10:30','COMPLETED','TCM consultation'),
(9,1,5,'2026-03-14','11:00-11:45','COMPLETED','Full screening'),
(9,5,3,'2026-03-21','13:30-13:55','CANCELLED','Eye care cancelled'),
(9,3,2,'2026-03-24','09:00-09:30','COMPLETED','Dental cleaning'),
(9,2,6,'2026-03-27','14:00-14:15','COMPLETED','Vaccination'),
(10,5,1,'2026-03-26','11:00-11:20','COMPLETED','Routine checkup'),
(10,1,2,'2026-03-28','14:00-14:30','NO_SHOW','Dental no-show'),
(10,2,5,'2026-03-30','09:00-09:45','COMPLETED','Screening visit'),
(6,1,1,'2026-04-02','09:00-09:20','BOOKED','General checkup'),
(6,2,4,'2026-04-05','14:00-14:30','CONFIRMED','TCM follow-up'),
(6,3,3,'2026-04-08','11:00-11:25','PENDING','Eye re-check'),
(6,4,5,'2026-04-12','09:30-10:15','BOOKED','Health screening'),
(7,2,1,'2026-04-03','09:30-09:50','CONFIRMED','General consult'),
(7,3,2,'2026-04-06','13:00-13:30','BOOKED','Dental cleaning'),
(7,4,5,'2026-04-10','15:00-15:45','PENDING','Health screening'),
(8,3,1,'2026-04-04','08:30-08:50','CONFIRMED','General visit'),
(8,5,5,'2026-04-07','09:00-09:45','BOOKED','Health check'),
(8,4,1,'2026-04-11','16:00-16:20','PENDING','Follow-up'),
(9,4,4,'2026-04-01','10:00-10:30','BOOKED','TCM session'),
(9,1,5,'2026-04-09','11:00-11:45','CONFIRMED','Full health check');

-- Queue entries (uses patient_id 1..5)
INSERT INTO queue_entries (clinic_id, service_id, patient_id, appointment_id, queue_date, token_no, priority_level, queue_status) VALUES
(1,1,1,1,  CURDATE(),1,0,'WAITING'),
(1,2,2,2,  CURDATE(),2,0,'CALLED'),
(1,5,3,3,  CURDATE(),3,1,'IN_SERVICE'),
(2,1,1,4,  CURDATE(),1,0,'WAITING'),
(2,4,2,5,  CURDATE(),2,0,'IN_SERVICE'),
(3,1,3,6,  CURDATE(),1,0,'CALLED'),
(3,2,4,7,  CURDATE(),2,0,'WAITING'),
(4,4,5,8,  CURDATE(),1,0,'WAITING'),
(4,5,1,9,  CURDATE(),2,1,'CALLED'),
(5,1,2,10, CURDATE(),1,0,'IN_SERVICE'),
(1,2,2,11, DATE_SUB(CURDATE(), INTERVAL 1 DAY),1,0,'COMPLETED'),
(1,6,1,12, DATE_SUB(CURDATE(), INTERVAL 1 DAY),2,0,'COMPLETED'),
(2,5,3,13, DATE_SUB(CURDATE(), INTERVAL 2 DAY),1,0,'COMPLETED'),
(3,3,4,14, DATE_SUB(CURDATE(), INTERVAL 2 DAY),1,0,'COMPLETED'),
(4,1,5,15, DATE_SUB(CURDATE(), INTERVAL 3 DAY),1,0,'COMPLETED'),
(5,3,3,16, DATE_SUB(CURDATE(), INTERVAL 3 DAY),1,0,'COMPLETED'),
(1,7,1,17, DATE_SUB(CURDATE(), INTERVAL 5 DAY),1,0,'SKIPPED'),
(2,9,2,18, DATE_SUB(CURDATE(), INTERVAL 5 DAY),2,0,'COMPLETED'),
(3,8,4,19, DATE_SUB(CURDATE(), INTERVAL 6 DAY),1,0,'SKIPPED'),
(5,7,5,20, DATE_SUB(CURDATE(), INTERVAL 7 DAY),2,0,'COMPLETED'),
(1,3,1,21, DATE_SUB(CURDATE(), INTERVAL 7 DAY),3,1,'COMPLETED'),
(4,10,2,22,DATE_SUB(CURDATE(), INTERVAL 4 DAY),2,0,'COMPLETED');

-- Notifications (patients, staff, admin)
INSERT INTO notifications (user_id, title, message, notification_type, related_appointment_id, is_read) VALUES
(6,'Appointment Booked','Your general consultation is booked.','appointment',1,0),
(6,'Appointment Reminder','Reminder for dental check-up.','reminder',2,0),
(6,'Appointment Completed','Your eye care visit is completed.','result',4,1),
(6,'Payment Received','Payment received successfully.','payment',1,1),
(6,'Welcome','Welcome to CCHC clinic system.','welcome',NULL,1),
(7,'Queue Update','You are next in queue.','queue',8,0),
(7,'Results Ready','Your screening results are ready.','result',10,1),
(7,'Appointment Cancelled','Your appointment was cancelled.','appointment',13,1),
(7,'Appointment Reminder','Upcoming appointment tomorrow.','reminder',9,0),
(7,'Feedback Request','Please rate your recent visit.','feedback',10,0),
(8,'Appointment Confirmed','Your appointment is confirmed.','appointment',17,0),
(8,'Package Booked','Health screening package booked.','appointment',16,0),
(8,'Counseling Session','Your counseling session is confirmed.','appointment',20,0),
(8,'Lab Results','Lab results are now available.','result',16,0),
(8,'Reminder','Reminder for TCM session.','reminder',17,0),
(9,'Appointment Confirmed','Your TCM consultation is confirmed.','appointment',22,0),
(9,'Screening Booked','Full health screening confirmed.','appointment',23,0),
(9,'No Show Alert','You missed your appointment.','alert',24,0),
(9,'Vaccination Reminder','Please confirm vaccination slot.','reminder',26,1),
(9,'BP Check Available','Blood pressure check available.','announcement',NULL,0),
(10,'Routine Booked','Routine checkup is booked.','appointment',27,0),
(10,'Dental Scheduled','Dental session scheduled.','appointment',28,0),
(10,'Screening Confirmed','Screening visit confirmed.','appointment',29,0),
(10,'No Show Notice','You missed your dental session.','alert',28,1),
(10,'Service Update','New services are available.','announcement',NULL,0),
(3,'New Appointment','New patient appointment assigned.','appointment',1,0),
(3,'Walk-in Check-in','A walk-in patient checked in.','queue',NULL,1),
(3,'Shift Start','Your shift starts in 30 minutes.','announcement',NULL,0),
(4,'Patient Arrival','Patient has arrived at front desk.','appointment',8,0),
(4,'Queue Status','Queue is active for clinic 2.','queue',NULL,1),
(5,'Daily Briefing','Today has high patient load.','announcement',NULL,0),
(1,'Daily Report','Daily report is ready.','report',NULL,0),
(1,'System Backup','Backup completed at 02:15.','system',NULL,1),
(1,'Utilisation Report','Monthly utilisation report available.','report',NULL,0),
(2,'No-show Analysis','Weekly no-show analysis available.','alert',NULL,0),
(2,'Policy Update','Max bookings per patient updated.','system',NULL,1);

-- Audit logs
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, created_at) VALUES
(3,'OPERATIONAL_ISSUE','CLINIC','1','Printer out of paper at front desk.',DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4,'OPERATIONAL_ISSUE','CLINIC','2','Queue display rebooted after freeze.',DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5,'OPERATIONAL_ISSUE','CLINIC','3','Dental chair maintenance required.',DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3,'OPERATIONAL_ISSUE','CLINIC','4','Water dispenser leak fixed.',DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4,'OPERATIONAL_ISSUE','CLINIC','5','Network issue restored in 20 minutes.',DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1,'LOGIN','USER','1','Admin logged in successfully.',DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1,'CSV_IMPORT','SERVICE','1','Imported services csv batch.',DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2,'REPORT_VIEW','REPORT','MONTHLY','Viewed monthly utilisation report.',DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- CSV import logs
INSERT INTO csv_import_logs (import_type, file_name, total_rows, success_rows, failed_rows, status, imported_by_user_id, imported_at) VALUES
('SERVICE','services_batch_1.csv',8,8,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 60 DAY)),
('SERVICE','services_batch_2.csv',5,5,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 50 DAY)),
('SERVICE','services_batch_3.csv',10,9,1,'PARTIAL',2,DATE_SUB(NOW(), INTERVAL 40 DAY)),
('SERVICE','clinic_services_1.csv',6,6,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 30 DAY)),
('SERVICE','dental_packages.csv',4,3,1,'PARTIAL',2,DATE_SUB(NOW(), INTERVAL 25 DAY)),
('SERVICE','tcm_services.csv',3,3,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 20 DAY)),
('SERVICE','screening_packages.csv',7,7,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 15 DAY)),
('SERVICE','wellness_services.csv',5,4,1,'PARTIAL',2,DATE_SUB(NOW(), INTERVAL 12 DAY)),
('SERVICE','vaccination_update.csv',6,6,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 8 DAY)),
('SERVICE','march_updates.csv',12,12,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SERVICE','latest_batch.csv',8,8,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 2 DAY)),
('SERVICE','today_batch.csv',5,5,0,'SUCCESS',1,DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Validation summary (quick check after import)
SELECT 'roles' AS table_name, COUNT(*) AS total_rows FROM roles
UNION ALL SELECT 'users', COUNT(*) FROM users
UNION ALL SELECT 'clinics', COUNT(*) FROM clinics
UNION ALL SELECT 'services', COUNT(*) FROM services
UNION ALL SELECT 'clinic_services', COUNT(*) FROM clinic_services
UNION ALL SELECT 'patient_profiles', COUNT(*) FROM patient_profiles
UNION ALL SELECT 'staff_profiles', COUNT(*) FROM staff_profiles
UNION ALL SELECT 'appointments', COUNT(*) FROM appointments
UNION ALL SELECT 'queue_entries', COUNT(*) FROM queue_entries
UNION ALL SELECT 'notifications', COUNT(*) FROM notifications
UNION ALL SELECT 'system_settings', COUNT(*) FROM system_settings
UNION ALL SELECT 'csv_import_logs', COUNT(*) FROM csv_import_logs
UNION ALL SELECT 'audit_logs', COUNT(*) FROM audit_logs;

-- End of sample data
