# CareFlow — Hospital Management System

CareFlow is a Java 17 / Spring Boot 3.3 application with Thymeleaf pages, Spring Security, Spring Data JPA, and MySQL.

## Requirements

- JDK 17 or later (the project compiles against Java 17)
- Apache Maven 3.9+
- MySQL Server 8.0+

## Local setup (Windows PowerShell)

1. Start the MySQL Windows service.
2. Create the database (or let the configured JDBC URL create it):

   ```sql
   CREATE DATABASE IF NOT EXISTS hospital_db;
   ```

3. Set environment variables in the same PowerShell window. Do not commit a real password to Git:

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
   $env:Path = "$env:JAVA_HOME\bin;C:\Program Files\apache-maven-3.9.14\bin;$env:Path"
   $env:DB_URL = "jdbc:mysql://localhost:3306/hospital_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "YOUR_LOCAL_MYSQL_PASSWORD"
   ```

4. Run from the repository root:

   ```powershell
   mvn -f backend\pom.xml spring-boot:run
   ```

5. Open `http://localhost:8082`.

The application currently uses `spring.jpa.hibernate.ddl-auto=update`; for production, use versioned migrations and a least-privilege database account instead of a root account.

## Role dashboards

- Patient: `/patient/dashboard`
- Receptionist: `/receptionist/dashboard`
- Doctor: `/doctor/dashboard`
- Pharmacy: `/pharmacy/dashboard`
- Laboratory: `/laboratory/dashboard`
- Billing: `/billing/dashboard`
- Staff / Nursing: `/staff/dashboard`
- Administration: `/admin/dashboard`

The laboratory, billing, staff/nursing, and administration pages added in this iteration are initial operational dashboards, not a claim that every workflow in the architecture diagram is complete. New accounts must be assigned the appropriate role by a trusted administrator; public self-registration should not be extended to privileged roles.

## Project documents

- `docs/PROJECT_AUDIT_AND_IMPLEMENTATION.md` — source inventory, reference comparison, gaps, security notes, and implementation status.
- `.env.example` — environment variable names (replace placeholder values locally).


## M3/M4 Batch 03 additions

See `IMPLEMENTATION_PROGRESS_M3_M4_BATCH_03.md` for scope, limitations and verification steps, and `LOC_REPORT_BATCH_03.txt` for the before/after Java source-line counts.

New routes:
- `/staff/wards` — ward and bed inventory
- `/staff/attendance` — staff check-in/check-out and team attendance
- `/admin/audit` — audit trail (Admin)
- `/admin/settings` — non-secret system settings (Admin)
- `/reports` — cross-module reports and CSV exports

This batch has not been compiled or tested in this packaging environment. Run `mvn -f backend/pom.xml test` from the project root before merging.
