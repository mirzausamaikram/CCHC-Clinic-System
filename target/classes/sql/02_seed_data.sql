USE cchc_clinic_system;

-- ============================================================
--  CCHC Clinic System – Full Sample Data
--  Run after 01_schema.sql (clean database).
--  All passwords stored as plain text per project convention.
-- ============================================================

-- ------------------------------------------------------------
-- ROLES
-- ------------------------------------------------------------
INSERT INTO roles (role_name, role_description) VALUES
('PATIENT', 'Clinic patient who books appointments'),
('STAFF',   'Clinic staff who manages appointments and queues'),
('ADMIN',   'System administrator with full access');

-- ------------------------------------------------------------
-- CLINICS (5)
-- ------------------------------------------------------------
INSERT INTO clinics (clinic_code, clinic_name, address_line1, city, state, phone, opening_time, closing_time, is_active) VALUES
('CL001', 'CCHC Central',        '1 Connaught Road Central',  'Central',       'Hong Kong', '2525-0000', '08:00:00', '18:00:00', 1),
('CL002', 'CCHC Tsim Sha Tsui',  '10 Granville Road',         'Tsim Sha Tsui', 'Kowloon',   '2315-1111', '08:00:00', '18:00:00', 1),
('CL003', 'CCHC Mong Kok',       '50 Argyle Street',          'Mong Kok',      'Kowloon',   '2397-2222', '08:00:00', '18:00:00', 1),
('CL004', 'CCHC Kowloon Bay',    '39 Kowloon Bay Road',       'Kowloon Bay',   'Kowloon',   '2796-3333', '08:00:00', '18:00:00', 1),
('CL005', 'CCHC Causeway Bay',   '100 Lockhart Road',         'Causeway Bay',  'Hong Kong', '2577-4444', '08:00:00', '18:00:00', 1);

-- ------------------------------------------------------------
-- SERVICES (5)
-- ------------------------------------------------------------
INSERT INTO services (service_code, service_name, service_description, default_duration_minutes, is_active) VALUES
('SRV001', 'General Consultation',          'General medical consultation and diagnosis',          20, 1),
('SRV002', 'Dental Check-up',               'Dental examination and professional cleaning',         30, 1),
('SRV003', 'Eye Care',                      'Comprehensive eye examination and vision testing',     25, 1),
('SRV004', 'Traditional Chinese Medicine',  'TCM consultation, acupuncture and herbal remedy',      30, 1),
('SRV005', 'Health Screening',              'Full body health screening package',                   45, 1);

-- ------------------------------------------------------------
-- CLINIC SERVICES (which service is offered at which clinic)
-- ------------------------------------------------------------
INSERT INTO clinic_services (clinic_id, service_id, duration_minutes, fee, is_active) VALUES
(1,1,20,100.00,1),(1,2,30,200.00,1),(1,3,25,150.00,1),(1,5,45,280.00,1),
(2,1,20, 95.00,1),(2,4,30,180.00,1),(2,5,45,280.00,1),
(3,1,20, 90.00,1),(3,2,30,190.00,1),(3,3,25,140.00,1),(3,4,30,170.00,1),
(4,1,20,100.00,1),(4,4,30,180.00,1),(4,5,45,300.00,1),
(5,1,20,110.00,1),(5,3,25,160.00,1),(5,5,45,320.00,1);

-- ------------------------------------------------------------
-- USERS
--   role_id 3 = ADMIN  (user_id  1 –  3)
--   role_id 2 = STAFF  (user_id  4 – 13)
--   role_id 1 = PATIENT(user_id 14 – 23)
-- ------------------------------------------------------------
INSERT INTO users (role_id, username, email, password_hash, is_active) VALUES
-- 3 admins
(3, 'admin',    'admin@cchc.hk',     'password', 1),
(3, 'admin2',   'admin2@cchc.hk',    'password', 1),
(3, 'admin3',   'admin3@cchc.hk',    'password', 1),
-- 10 staff
(2, 'staff1',   'staff1@cchc.hk',    'password', 1),
(2, 'staff2',   'staff2@cchc.hk',    'password', 1),
(2, 'staff3',   'staff3@cchc.hk',    'password', 1),
(2, 'staff4',   'staff4@cchc.hk',    'password', 1),
(2, 'staff5',   'staff5@cchc.hk',    'password', 1),
(2, 'staff6',   'staff6@cchc.hk',    'password', 1),
(2, 'staff7',   'staff7@cchc.hk',    'password', 1),
(2, 'staff8',   'staff8@cchc.hk',    'password', 1),
(2, 'staff9',   'staff9@cchc.hk',    'password', 1),
(2, 'staff10',  'staff10@cchc.hk',   'password', 1),
-- 10 patients
(1, 'patient1',  'patient1@cchc.hk',  'password', 1),
(1, 'patient2',  'patient2@cchc.hk',  'password', 1),
(1, 'patient3',  'patient3@cchc.hk',  'password', 1),
(1, 'patient4',  'patient4@cchc.hk',  'password', 1),
(1, 'patient5',  'patient5@cchc.hk',  'password', 1),
(1, 'patient6',  'patient6@cchc.hk',  'password', 1),
(1, 'patient7',  'patient7@cchc.hk',  'password', 1),
(1, 'patient8',  'patient8@cchc.hk',  'password', 1),
(1, 'patient9',  'patient9@cchc.hk',  'password', 1),
(1, 'patient10', 'patient10@cchc.hk', 'password', 1);

-- ------------------------------------------------------------
-- PATIENT PROFILES (user_id 14-23 → patient_id 1-10)
-- ------------------------------------------------------------
INSERT INTO patient_profiles (user_id, full_name, phone) VALUES
(14, 'Wong Ming Ho',   '9876-5401'),
(15, 'Lee Sue Lin',    '9876-5402'),
(16, 'Chan Yuk Mei',   '9876-5403'),
(17, 'Tang Chi Hung',  '9876-5404'),
(18, 'Cheung Wai Kin', '9876-5405'),
(19, 'Ho Ka Man',      '9876-5406'),
(20, 'Ng Siu Wah',     '9876-5407'),
(21, 'Lau Mei Ling',   '9876-5408'),
(22, 'Yip Tsz Hin',    '9876-5409'),
(23, 'Fong Bik Ha',    '9876-5410');

-- ------------------------------------------------------------
-- STAFF PROFILES (user_id 4-13 → 2 staff per clinic)
-- ------------------------------------------------------------
INSERT INTO staff_profiles (user_id, clinic_id, employee_no, full_name, phone, position_title) VALUES
( 4, 1, 'EMP-1001', 'Lam Hok Ming',   '2525-0001', 'Front Desk Officer'),
( 5, 1, 'EMP-1002', 'Poon Ying Nian', '2525-0002', 'Clinic Nurse'),
( 6, 2, 'EMP-1003', 'Kwok Siu Fai',   '2315-0001', 'Clinic Assistant'),
( 7, 2, 'EMP-1004', 'Mak Wai Han',    '2315-0002', 'Receptionist'),
( 8, 3, 'EMP-1005', 'Tsang Hoi Yee',  '2397-0001', 'Front Desk Officer'),
( 9, 3, 'EMP-1006', 'Yuen Chun Wai',  '2397-0002', 'Clinic Assistant'),
(10, 4, 'EMP-1007', 'Chow Kin Lam',   '2796-0001', 'Clinic Nurse'),
(11, 4, 'EMP-1008', 'Mui Ka Wai',     '2796-0002', 'Receptionist'),
(12, 5, 'EMP-1009', 'Sze Ho Yin',     '2577-0001', 'Front Desk Officer'),
(13, 5, 'EMP-1010', 'To Mei Fun',     '2577-0002', 'Clinic Assistant');

-- ------------------------------------------------------------
-- SYSTEM SETTINGS
-- ------------------------------------------------------------
INSERT INTO system_settings (setting_key, setting_value, description, updated_by_user_id) VALUES
('maxBookingsPerPatient',            '3',  'Max active bookings per patient',     1),
('cancellationCutoffHours',          '24', 'Cancellation cutoff in hours',        1),
('MAX_DAILY_APPOINTMENTS_PER_PATIENT','3', 'Legacy key – max daily appointments', 1),
('queueEnabled',                     '1',  'Global queue enable flag',            1),
('queueEnabled_clinic_1',            '1',  'Queue enabled for CL001 Central',     1),
('queueEnabled_clinic_2',            '1',  'Queue enabled for CL002 TST',         1),
('queueEnabled_clinic_3',            '1',  'Queue enabled for CL003 MK',          1),
('queueEnabled_clinic_4',            '1',  'Queue enabled for CL004 KB',          1),
('queueEnabled_clinic_5',            '1',  'Queue enabled for CL005 CB',          1);

-- ------------------------------------------------------------
-- APPOINTMENTS (appointment_id 1-40, ~4 per patient)
-- Valid clinic→service combos used:
--   CL1: SRV1,2,3,5  CL2: SRV1,4,5  CL3: SRV1,2,3,4
--   CL4: SRV1,4,5    CL5: SRV1,3,5
-- ------------------------------------------------------------
INSERT INTO appointments (user_id, clinic_id, service_id, appointment_date, time_slot, status, notes) VALUES
-- patient1 (user_id 14)
(14,1,1, CURDATE(),                         '09:00-09:20','BOOKED',     'Morning checkup'),
(14,1,2, DATE_ADD(CURDATE(),INTERVAL 2 DAY),'10:00-10:30','CONFIRMED',  'Dental cleaning follow-up'),
(14,2,4, DATE_ADD(CURDATE(),INTERVAL 5 DAY),'14:00-14:30','BOOKED',     'TCM – first visit'),
(14,3,3, DATE_SUB(CURDATE(),INTERVAL 7 DAY),'11:00-11:25','COMPLETED',  'Eye test done'),

-- patient2 (user_id 15)
(15,2,1, CURDATE(),                         '09:30-09:50','ARRIVED',    'Flu symptoms'),
(15,3,2, DATE_ADD(CURDATE(),INTERVAL 1 DAY),'13:00-13:30','BOOKED',     'Dental check'),
(15,4,5, DATE_SUB(CURDATE(),INTERVAL 3 DAY),'15:00-15:45','COMPLETED',  'Annual screening'),
(15,1,3, DATE_ADD(CURDATE(),INTERVAL 6 DAY),'10:30-10:55','CONFIRMED',  'Eye exam'),

-- patient3 (user_id 16)
(16,3,1, CURDATE(),                         '08:30-08:50','CHECKED_IN', 'General consultation'),
(16,5,5, DATE_ADD(CURDATE(),INTERVAL 3 DAY),'09:00-09:45','BOOKED',     'Health package A'),
(16,2,4, DATE_SUB(CURDATE(),INTERVAL 5 DAY),'14:30-15:00','COMPLETED',  'TCM – knee pain'),
(16,4,1, DATE_ADD(CURDATE(),INTERVAL 8 DAY),'16:00-16:20','PENDING',    'Referral follow-up'),

-- patient4 (user_id 17)
(17,4,4, CURDATE(),                         '10:00-10:30','BOOKED',     'TCM consultation'),
(17,1,5, DATE_ADD(CURDATE(),INTERVAL 2 DAY),'11:00-11:45','CONFIRMED',  'Full screening'),
(17,5,3, DATE_SUB(CURDATE(),INTERVAL 2 DAY),'13:30-13:55','CANCELLED',  'Patient cancelled'),
(17,3,2, DATE_SUB(CURDATE(),INTERVAL 10 DAY),'09:00-09:30','COMPLETED', 'Dental done'),

-- patient5 (user_id 18)
(18,5,1, CURDATE(),                         '11:00-11:20','BOOKED',     'Routine checkup'),
(18,1,2, DATE_ADD(CURDATE(),INTERVAL 4 DAY),'14:00-14:30','BOOKED',     'Tooth extraction follow-up'),
(18,2,5, DATE_SUB(CURDATE(),INTERVAL 6 DAY),'09:00-09:45','COMPLETED',  'Screening done'),
(18,3,1, DATE_ADD(CURDATE(),INTERVAL 9 DAY),'10:00-10:20','PENDING',    'Second opinion'),

-- patient6 (user_id 19)
(19,1,3, CURDATE(),                         '13:00-13:25','ARRIVED',    'Vision correction check'),
(19,4,5, DATE_ADD(CURDATE(),INTERVAL 1 DAY),'15:00-15:45','BOOKED',     'Health screening'),
(19,2,1, DATE_SUB(CURDATE(),INTERVAL 4 DAY),'08:30-08:50','NO_SHOW',    'Patient did not arrive'),
(19,5,5, DATE_ADD(CURDATE(),INTERVAL 7 DAY),'09:30-10:15','CONFIRMED',  'Premiere package'),

-- patient7 (user_id 20)
(20,3,4, CURDATE(),                         '14:00-14:30','BOOKED',     'Back pain treatment'),
(20,2,1, DATE_ADD(CURDATE(),INTERVAL 3 DAY),'10:30-10:50','BOOKED',     'General checkup'),
(20,1,1, DATE_SUB(CURDATE(),INTERVAL 8 DAY),'09:00-09:20','COMPLETED',  'Checkup completed'),
(20,4,4, DATE_ADD(CURDATE(),INTERVAL 6 DAY),'11:00-11:30','PENDING',    'TCM second session'),

-- patient8 (user_id 21)
(21,5,3, CURDATE(),                         '15:00-15:25','BOOKED',     'Eye follow-up'),
(21,3,3, DATE_ADD(CURDATE(),INTERVAL 2 DAY),'13:00-13:25','CONFIRMED',  'Prescription renewal'),
(21,1,2, DATE_SUB(CURDATE(),INTERVAL 9 DAY),'10:00-10:30','COMPLETED',  'Dental cleaning done'),
(21,2,1, DATE_ADD(CURDATE(),INTERVAL 5 DAY),'08:30-08:50','BOOKED',     'Routine checkup'),

-- patient9 (user_id 22)
(22,4,1, CURDATE(),                         '09:00-09:20','CONFIRMED',  'Blood pressure check'),
(22,5,5, DATE_ADD(CURDATE(),INTERVAL 4 DAY),'14:30-15:15','BOOKED',     'Premium screening'),
(22,3,2, DATE_SUB(CURDATE(),INTERVAL 3 DAY),'11:30-12:00','CANCELLED',  'Cancelled – clinic busy'),
(22,1,1, DATE_SUB(CURDATE(),INTERVAL 12 DAY),'08:30-08:50','NO_SHOW',   'No show recorded'),

-- patient10 (user_id 23)
(23,2,4, CURDATE(),                         '16:00-16:30','BOOKED',     'TCM – migraine'),
(23,4,5, DATE_ADD(CURDATE(),INTERVAL 1 DAY),'11:00-11:45','CONFIRMED',  'Annual health check'),
(23,5,1, DATE_SUB(CURDATE(),INTERVAL 1 DAY),'09:30-09:50','COMPLETED',  'Post-op follow-up'),
(23,3,4, DATE_ADD(CURDATE(),INTERVAL 10 DAY),'14:00-14:30','PENDING',   'Herbal remedy session');

-- ------------------------------------------------------------
-- QUEUE ENTRIES (10 entries for today)
-- patient_id = row number in patient_profiles table (1-10)
-- ------------------------------------------------------------
INSERT INTO queue_entries (clinic_id, service_id, patient_id, appointment_id, queue_date, token_no, priority_level, queue_status) VALUES
(1,1,1,  1, CURDATE(),  1, 0, 'WAITING'),
(2,1,2,  5, CURDATE(),  1, 0, 'IN_SERVICE'),
(3,1,3,  9, CURDATE(),  1, 1, 'CALLED'),
(4,4,4, 13, CURDATE(),  1, 0, 'WAITING'),
(5,1,5, 17, CURDATE(),  1, 0, 'WAITING'),
(1,3,6, 21, CURDATE(),  2, 1, 'CALLED'),
(3,4,7, 25, CURDATE(),  2, 0, 'WAITING'),
(5,3,8, 29, CURDATE(),  2, 0, 'IN_SERVICE'),
(4,1,9, 33, CURDATE(),  2, 0, 'WAITING'),
(2,4,10,37, CURDATE(),  2, 1, 'WAITING');

-- ------------------------------------------------------------
-- NOTIFICATIONS
--   10 for patients, 10 for staff, 10 for admin
--   (related_appointment_id must reference existing appointments)
-- ------------------------------------------------------------
INSERT INTO notifications (user_id, title, message, notification_type, related_appointment_id, is_read) VALUES
-- --- PATIENT NOTIFICATIONS (10) ---
(14,'Appointment Confirmed',       'Your General Consultation at CCHC Central is confirmed for today at 09:00.',                         'appointment', 1,  0),
(14,'Dental Reminder',             'Reminder: Dental Check-up at CCHC Central in 2 days at 10:00. Please arrive 10 min early.',         'reminder',    2,  0),
(15,'Arrived – You Are Next',      'You have checked in at CCHC Tsim Sha Tsui. Token #1. Please wait in the lobby.',                     'queue',       5,  0),
(15,'Screening Complete',          'Your Health Screening at CCHC Kowloon Bay is complete. Results will be sent within 3 working days.', 'result',      7,  1),
(16,'Queue Called',                'Token #1 – please proceed to Counter 2 at CCHC Mong Kok.',                                          'queue',       9,  0),
(17,'Appointment Cancelled',       'Your Eye Care appointment on has been cancelled as requested.',                                      'appointment', 15, 1),
(18,'Booking Confirmation',        'Your appointment at CCHC Causeway Bay (General Consultation) is booked for today at 11:00.',         'appointment', 17, 0),
(19,'No-Show Notice',              'You missed your appointment at CCHC Tsim Sha Tsui on the 3rd. Please reschedule.',                  'appointment', 23, 0),
(21,'Eye Follow-up Ready',         'Your follow-up appointment at CCHC Causeway Bay is confirmed for today at 15:00.',                  'appointment', 29, 0),
(22,'Cancellation Confirmed',      'Your Dental appointment at CCHC Mong Kok has been successfully cancelled.',                         'appointment', 35, 1),

-- --- STAFF NOTIFICATIONS (10) ---
( 4,'New Appointment Today',       'Wong Ming Ho – General Consultation at 09:00. Please prepare Counter 1.',                           'appointment', 1,  0),
( 4,'Walk-in Arrived',             'Walk-in patient Fong Bik Ha checked in for TCM at CCHC Tsim Sha Tsui. Token #2.',                   'queue',       NULL, 1),
( 5,'Queue Update',                '3 patients now waiting at CCHC Central. Please open an additional counter.',                        'queue',       NULL, 0),
( 5,'Shift Reminder',              'Your afternoon shift starts at 13:00 today. Please confirm attendance.',                            'announcement',NULL, 0),
( 6,'Patient Arrived',             'Lee Sue Lin has arrived for General Consultation. Token #1 at CCHC Tsim Sha Tsui.',                  'appointment', 5,  0),
( 7,'No-Show Logged',              'Ho Ka Man did not attend the 08:30 appointment at CCHC Tsim Sha Tsui. Logged as NO_SHOW.',          'appointment', 23, 1),
( 8,'Screening Slot Opening',      'Health Screening slot at 15:00 is now available at CCHC Mong Kok.',                                'announcement',NULL, 0),
( 9,'Patient Check-in',            'Chan Yuk Mei checked in for General Consultation. Queue status: CHECKED_IN.',                       'appointment', 9,  0),
(10,'TCM Appointment Reminder',    'Tang Chi Hung – TCM Consultation at CCHC Kowloon Bay at 10:00 today.',                             'appointment', 13, 0),
(11,'End of Day Summary',          '8 appointments processed today at CCHC Kowloon Bay. 1 no-show, 0 cancellations.',                  'announcement',NULL, 1),

-- --- ADMIN NOTIFICATIONS (10) ---
( 1,'Daily Backup Complete',       'Automated database backup completed successfully at 02:00.',                                        'system',      NULL, 1),
( 1,'New Patient Registered',      'Fong Bik Ha (patient10) has registered a new patient account.',                                    'user',        NULL, 0),
( 1,'No-Show Alert',               '2 no-show appointments recorded today. Recommend patient follow-up.',                              'alert',       NULL, 0),
( 1,'Utilisation Report Ready',    'Daily report: Central 78%, TST 65%, MK 82%, KB 70%, CB 60% utilisation.',                          'report',      NULL, 0),
( 1,'CSV Import Success',          'Admin imported 5 new services via CSV at 10:15. Review in Import Logs.',                           'system',      NULL, 1),
( 2,'System Maintenance',          'Scheduled DB maintenance tonight 02:00–04:00. System will be in read-only mode.',                  'system',      NULL, 1),
( 2,'Queue Overflow Warning',      'CCHC Mong Kok queue exceeded 10 patients. Consider opening extra counter.',                        'alert',       NULL, 0),
( 2,'Monthly Summary',             'March 2026: 312 appointments, 94% satisfaction rate, 8 incident reports filed.',                   'report',      NULL, 0),
( 3,'New Staff Added',             'Sze Ho Yin (staff9) onboarded and assigned to CCHC Causeway Bay.',                                 'user',        NULL, 1),
( 3,'Policy Updated',              'Max bookings per patient changed from 2 to 3 by admin. Effective immediately.',                    'system',      NULL, 0);

-- ------------------------------------------------------------
-- INCIDENT LOGS (10 entries in audit_logs, action=OPERATIONAL_ISSUE)
-- These appear in Admin → Incident Logs page
-- ------------------------------------------------------------
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, created_at) VALUES
( 4,'OPERATIONAL_ISSUE','CLINIC','1',  'Printer at CCHC Central front desk out of paper. Replaced at 09:15.',       DATE_SUB(NOW(), INTERVAL 1  DAY)),
( 5,'OPERATIONAL_ISSUE','CLINIC','1',  'Air conditioning unit in waiting room malfunctioned. Reported to facilities.',DATE_SUB(NOW(), INTERVAL 2  DAY)),
( 6,'OPERATIONAL_ISSUE','CLINIC','2',  'Queue display screen froze for 20 mins. System restarted at 11:00.',         DATE_SUB(NOW(), INTERVAL 3  DAY)),
( 7,'OPERATIONAL_ISSUE','CLINIC','2',  'Patient complaint: long wait time at Tsim Sha Tsui – average 40 min.',       DATE_SUB(NOW(), INTERVAL 4  DAY)),
( 8,'OPERATIONAL_ISSUE','CLINIC','3',  'Dental chair #2 reported broken – out of service until further notice.',     DATE_SUB(NOW(), INTERVAL 5  DAY)),
( 9,'OPERATIONAL_ISSUE','CLINIC','3',  'Barcode scanner at CCHC Mong Kok reception unresponsive. Replaced.',         DATE_SUB(NOW(), INTERVAL 6  DAY)),
(10,'OPERATIONAL_ISSUE','CLINIC','4',  'Water dispenser leak in patient waiting area. Cleaned up, maintenance called.',DATE_SUB(NOW(), INTERVAL 7 DAY)),
(11,'OPERATIONAL_ISSUE','CLINIC','4',  'TCM consultation room temperature too low – thermostat adjusted.',           DATE_SUB(NOW(), INTERVAL 8  DAY)),
(12,'OPERATIONAL_ISSUE','CLINIC','5',  'Fire alarm false trigger at 14:30. Evacuated and re-entered after check.',   DATE_SUB(NOW(), INTERVAL 9  DAY)),
(13,'OPERATIONAL_ISSUE','CLINIC','5',  'Internet connectivity issue – booking system offline 20 min at 16:00.',      DATE_SUB(NOW(), INTERVAL 10 DAY));

-- Additional audit log entries (general system activity visible in logs/reports)
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, created_at) VALUES
( 1,'LOGIN','USER','1',   'Admin logged in from IP 192.168.1.1',                                    DATE_SUB(NOW(), INTERVAL 1  HOUR)),
( 1,'CSV_IMPORT','SERVICE','5', '5 services imported via CSV by admin. File: bulk_services.csv',    DATE_SUB(NOW(), INTERVAL 2  HOUR)),
( 1,'POLICY_UPDATE','SETTING','maxBookingsPerPatient', 'Changed from 2 to 3',                       DATE_SUB(NOW(), INTERVAL 3  HOUR)),
(14,'BOOK_APPOINTMENT','APPOINTMENT','1', 'Patient booked General Consultation at CL001',            DATE_SUB(NOW(), INTERVAL 4  HOUR)),
(15,'BOOK_APPOINTMENT','APPOINTMENT','5', 'Patient booked General Consultation at CL002',            DATE_SUB(NOW(), INTERVAL 5  HOUR)),
(16,'CANCEL_APPOINTMENT','APPOINTMENT','12','Patient cancelled TCM appointment at CL002',            DATE_SUB(NOW(), INTERVAL 6  HOUR)),
( 4,'QUEUE_UPDATE','QUEUE','1', 'Staff called Token #1 at CCHC Central',                            DATE_SUB(NOW(), INTERVAL 7  HOUR)),
( 6,'QUEUE_UPDATE','QUEUE','2', 'Staff started service for Token #1 at CCHC Tsim Sha Tsui',         DATE_SUB(NOW(), INTERVAL 8  HOUR)),
( 2,'USER_CREATE','USER','23', 'Admin created new patient account patient10',                        DATE_SUB(NOW(), INTERVAL 1  DAY)),
( 1,'REPORT_VIEW','REPORT','CL001', 'Admin viewed monthly report for CCHC Central – March 2026',    DATE_SUB(NOW(), INTERVAL 2  DAY));

-- ------------------------------------------------------------
-- CSV IMPORT LOGS (10 entries – visible in Admin → CSV Import)
-- ------------------------------------------------------------
INSERT INTO csv_import_logs (import_type, file_name, total_rows, success_rows, failed_rows, status, imported_by_user_id, imported_at) VALUES
('SERVICE', 'bulk_services_jan.csv',      8,  8,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 60 DAY)),
('SERVICE', 'new_services_feb.csv',       5,  5,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 45 DAY)),
('SERVICE', 'services_update_mar.csv',   10,  9,  1, 'PARTIAL',        2, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('SERVICE', 'quarterly_services.csv',     6,  6,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
('SERVICE', 'dental_services_add.csv',    4,  3,  1, 'PARTIAL',        2, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('SERVICE', 'tcm_services_new.csv',       3,  3,  0, 'SUCCESS',        3, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('SERVICE', 'eye_care_packages.csv',      7,  7,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('SERVICE', 'screening_packages.csv',     5,  4,  1, 'PARTIAL',        2, DATE_SUB(NOW(), INTERVAL 8  DAY)),
('SERVICE', 'services_cleanup.csv',      12, 12,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 4  DAY)),
('SERVICE', 'latest_services_batch.csv',  6,  6,  0, 'SUCCESS',        1, DATE_SUB(NOW(), INTERVAL 1  DAY));

-- 5 services
INSERT INTO services (service_code, service_name, service_description, default_duration_minutes, is_active) VALUES
('SRV001', 'General Consultation', 'General medical consultation and diagnosis', 20, 1),
('SRV002', 'Dental Check-up', 'Dental examination and cleaning', 30, 1),
('SRV003', 'Eye Care', 'Eye examination and vision testing', 25, 1),
('SRV004', 'Traditional Chinese Medicine', 'TCM consultation and treatment', 30, 1),
('SRV005', 'Health Screening', 'Comprehensive health screening package', 45, 1);

-- clinic_services (service availability at each clinic)
INSERT INTO clinic_services (clinic_id, service_id, duration_minutes, fee, is_active) VALUES
(1,1,20,100.00,1),(1,2,30,200.00,1),(1,3,25,150.00,1),
(2,1,20,95.00,1),(2,4,30,180.00,1),(2,5,45,280.00,1),
(3,1,20,90.00,1),(3,2,30,190.00,1),(3,3,25,140.00,1),
(4,1,20,100.00,1),(4,4,30,180.00,1),(4,5,45,300.00,1),
(5,1,20,110.00,1),(5,3,25,160.00,1),(5,5,45,320.00,1);

-- 7 users (admin, 2 staff, 4 patients) password = password
INSERT INTO users (role_id, username, email, password_hash, is_active) VALUES
(3, 'admin', 'admin@cchc.hk', 'password', 1),
(3, 'admin2', 'admin2@cchc.hk', 'password', 1),
(2, 'staff1', 'staff1@cchc.hk', 'password', 1),
(2, 'staff2', 'staff2@cchc.hk', 'password', 1),
(1, 'patient1', 'patient1@cchc.hk', 'password', 1),
(1, 'patient2', 'patient2@cchc.hk', 'password', 1),
(1, 'patient3', 'patient3@cchc.hk', 'password', 1),
(1, 'patient4', 'patient4@cchc.hk', 'password', 1);

-- patient profile rows (HK names)
INSERT INTO patient_profiles (user_id, full_name, phone) VALUES
(5, 'Wong Ming Ho', '9876-5432'),
(6, 'Lee Sue Lin', '9876-5433'),
(7, 'Chan Yuk Mei', '9876-5434'),
(8, 'Tang Chi Hung', '9876-5435');

-- staff profile rows
INSERT INTO staff_profiles (user_id, clinic_id, employee_no, full_name, phone, position_title) VALUES
(3, 1, 'EMP-1001', 'Lam Hok Ming', '2525-0001', 'Front Desk Officer'),
(4, 2, 'EMP-1002', 'Poon Ying Nian', '2315-0001', 'Clinic Assistant');

-- system settings
INSERT INTO system_settings (setting_key, setting_value, description, updated_by_user_id) VALUES
('maxBookingsPerPatient', '3', 'max active bookings per patient', 1),
('cancellationCutoffHours', '24', 'cancel cutoff hours', 1),
('MAX_DAILY_APPOINTMENTS_PER_PATIENT', '3', 'old key support', 1);

-- sample appointments for patients (user_id 5-8 are patients)
INSERT INTO appointments (user_id, clinic_id, service_id, appointment_date, time_slot, status, notes) VALUES
(5, 1, 1, CURDATE(), '09:00-09:20', 'BOOKED', 'General checkup'),
(5, 1, 2, CURDATE(), '10:30-11:00', 'BOOKED', 'Dental cleaning'),
(5, 2, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00-11:25', 'CONFIRMED', 'Eye test'),
(5, 1, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00-09:20', 'PENDING', 'Follow-up'),
(6, 2, 4, CURDATE(), '13:00-13:30', 'ARRIVED', 'TCM consultation'),
(6, 3, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00-14:20', 'BOOKED', 'General checkup'),
(7, 4, 5, CURDATE(), '15:00-15:45', 'COMPLETED', 'Health screening'),
(7, 5, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00-10:25', 'CANCELLED', 'Eye care'),
(8, 1, 1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '10:30-10:50', 'NO_SHOW', 'Missed appointment'),
(8, 2, 2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '16:00-16:30', 'BOOKED', 'Dental check');

-- queue entries (walk-in patients)
INSERT INTO queue_entries (clinic_id, service_id, patient_id, appointment_id, queue_date, token_no, priority_level, queue_status) VALUES
(1, 1, 5, 1, CURDATE(), 1, 0, 'WAITING'),
(2, 4, 6, 5, CURDATE(), 1, 0, 'CALLED'),
(4, 5, 7, NULL, CURDATE(), 2, 1, 'IN_SERVICE');

-- comprehensive notifications for all users (patient, staff, admin)
INSERT INTO notifications (user_id, title, message, notification_type, related_appointment_id, is_read) VALUES
-- patient1 notifications
(5, 'Appointment Confirmed', 'Your appointment at CCHC Central on ' || DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d') || ' at 11:00 has been confirmed.', 'appointment', 3, 0),
(5, 'Appointment Reminder', 'Reminder: You have an appointment at CCHC Central tomorrow at 09:00. Please arrive 10 minutes early.', 'reminder', 1, 0),
(5, 'Payment Received', 'Thank you for your payment of HK$100 for General Consultation. Receipt #APT001', 'payment', 1, 1),
(5, 'Welcome to CCHC', 'Welcome to our clinic system. Your account has been created successfully.', 'welcome', NULL, 1),

-- patient2 notifications
(6, 'Appointment Completed', 'Your TCM consultation appointment has been completed. Thank you for visiting CCHC Tsim Sha Tsui.', 'appointment', 5, 1),
(6, 'New Booking Confirmation', 'Your appointment is booked for ' || DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '%Y-%m-%d') || ' at 14:00 at CCHC Mong Kok.', 'appointment', 6, 0),
(6, 'Service Feedback', 'How was your recent visit? Please share your feedback to help us improve.', 'feedback', 5, 0),

-- patient3 notifications
(7, 'Appointment Cancelled', 'Your Eye Care appointment on ' || DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d') || ' has been cancelled.', 'appointment', 8, 1),
(7, 'Health Screening Result', 'Your health screening results are now available. Please contact the clinic for details.', 'result', 7, 0),

-- patient4 notifications
(8, 'Missed Appointment Notice', 'You missed your appointment on ' || DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL -1 DAY), '%Y-%m-%d') || '. Please reschedule.', 'appointment', 9, 0),
(8, 'Appointment Upcoming', 'Reminder: Your appointment with CCHC Tsim Sha Tsui is coming up on ' || DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 5 DAY), '%Y-%m-%d') || '.', 'reminder', 10, 0),

-- staff1 notifications
(3, 'New Appointment Assigned', 'New appointment scheduled: Wong Ming Ho - General Consultation, Today 09:00-09:20', 'appointment', 1, 0),
(3, 'Queue Updated', 'Queue status updated: 2 patients waiting at CCHC Central', 'queue', NULL, 1),
(3, 'System Announcement', 'Clinic will close early today at 16:00 for staff meeting.', 'announcement', NULL, 0),

-- staff2 notifications
(4, 'Walk-in Check-in', 'Lee Sue Lin has checked in for TCM consultation. Token #1', 'queue', NULL, 0),
(4, 'Patient Arrived', 'Patient arrival: Chan Yuk Mei at 15:00 for Health Screening', 'appointment', 7, 1),
(4, 'End of Shift', 'Your shift ends in 30 minutes. Please ensure all patients are processed.', 'announcement', NULL, 0),

-- admin notifications
(1, 'System Backup', 'Daily system backup completed successfully at 23:00.', 'system', NULL, 1),
(1, 'New User Registration', 'Tang Chi Hung has registered as a new patient. User ID: 8', 'user', NULL, 0),
(1, 'Utilisation Report', 'Daily clinic utilisation report is ready. Central: 75%, Tsim Sha Tsui: 68%, Mong Kok: 82%', 'report', NULL, 0),
(1, 'No-Show Alert', '1 patient no-show today at CCHC Central. Follow-up recommended.', 'alert', 9, 0),

-- admin2 notifications
(2, 'System Message', 'Database maintenance scheduled for 02:00 - 04:00 tonight.', 'system', NULL, 1),
(2, 'Queue Status Summary', 'Queue summary: 5 waiting, 2 in service, 1 done today', 'queue', NULL, 0);
