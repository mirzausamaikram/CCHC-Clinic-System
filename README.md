# CCHC-Clinic-System

Simple student-built clinic management system for ITP4511 using Jakarta Servlets + JSP + JavaBeans + DAO + JDBC + MySQL.

## Features
- Login/logout with role-based dashboards (Patient, Staff, Admin)
- Appointment booking
- My bookings (cancel + reschedule)
- Walk-in queue management
- Notifications
- Admin user management + reports
- CSV import (extra feature)

## Tech Stack
- Java 11
- Jakarta EE 9.1 (Servlet/JSP)
- Maven (WAR project)
- MySQL 8+
- GlassFish 6+

## Project Structure
- `src/main/java/com/cchc/model` -> JavaBeans
- `src/main/java/com/cchc/dao` -> JDBC DAO classes
- `src/main/java/com/cchc/servlet` -> Controllers
- `src/main/webapp` -> JSP views
- `src/main/resources/sql` -> schema + seed scripts

## Database Setup
1. Create database in MySQL (example):
   - `CREATE DATABASE cchc_clinic_system;`
2. Run SQL scripts in this order:
   - `src/main/resources/sql/01_schema.sql`
   - `src/main/resources/sql/02_seed_data.sql`

## Run (NetBeans + GlassFish)
1. Open the project in NetBeans.
2. Make sure GlassFish server is configured.
3. Build and run the project.
4. Open in browser:
   - `http://localhost:8080/CCHC-Clinic-System/`

## Sample Login
Use users from your seeded `users` table, for example:
- Admin: `admin1` / `admin123`
- Staff: `staff1` / `staff123`
- Patient: `patient1` / `patient123`

## Notes
- This project uses simple student-style coding for assignment requirements.
- Demo passwords are plain text in seed data for local testing only.
- For production, use hashed passwords and stronger security hardening.
