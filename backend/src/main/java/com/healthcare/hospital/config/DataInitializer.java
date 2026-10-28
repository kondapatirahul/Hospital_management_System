package com.healthcare.hospital.config;

import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.*;
import com.healthcare.hospital.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           QueueEntryRepository queueEntryRepository,
                           MedicalRecordRepository medicalRecordRepository,
                           PrescriptionRepository prescriptionRepository,
                           LabReportRepository labReportRepository,
                           InvoiceRepository invoiceRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labReportRepository = labReportRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Seed data already present
        }

        // 1. Departments
        Department cardiology = departmentRepository.save(new Department("Cardiology", "Heart and cardiovascular system care", "heart-pulse"));
        Department neurology = departmentRepository.save(new Department("Neurology", "Brain, spine and nervous system disorders", "brain"));
        Department pediatrics = departmentRepository.save(new Department("Pediatrics", "Infant, child and adolescent health", "baby"));
        Department orthopedics = departmentRepository.save(new Department("Orthopedics", "Bones, joints, ligaments and tendons", "bone"));
        Department generalMedicine = departmentRepository.save(new Department("General Medicine", "Primary healthcare and preventive diagnostics", "stethoscope"));
        Department dermatology = departmentRepository.save(new Department("Dermatology", "Skin, hair and nail treatments", "sparkles"));

        // 2. Doctors
        Doctor drWright = doctorRepository.save(new Doctor(
                "Alexander Wright", "MD, FACC - Senior Cardiologist", cardiology,
                "dr.wright@hospital.com", "+1 (555) 101-0001", "Room 201",
                BigDecimal.valueOf(75.00), "Mon, Tue, Wed, Thu, Fri", "09:00 AM - 04:30 PM"));

        Doctor drGomez = doctorRepository.save(new Doctor(
                "Elena Gomez", "MD, PhD - Neurologist", neurology,
                "dr.gomez@hospital.com", "+1 (555) 101-0002", "Room 204",
                BigDecimal.valueOf(80.00), "Mon, Wed, Fri", "09:30 AM - 05:00 PM"));

        Doctor drChen = doctorRepository.save(new Doctor(
                "Robert Chen", "MD, FAAP - Pediatric Specialist", pediatrics,
                "dr.chen@hospital.com", "+1 (555) 101-0003", "Room 105",
                BigDecimal.valueOf(60.00), "Mon, Tue, Thu, Sat", "08:30 AM - 03:30 PM"));

        Doctor drPatel = doctorRepository.save(new Doctor(
                "Sarah Patel", "MS (Ortho), Fellowship in Joint Replacement", orthopedics,
                "dr.patel@hospital.com", "+1 (555) 101-0004", "Room 302",
                BigDecimal.valueOf(70.00), "Tue, Wed, Fri", "10:00 AM - 05:00 PM"));

        Doctor drVance = doctorRepository.save(new Doctor(
                "Marcus Vance", "MBBS, MD - General Physician", generalMedicine,
                "dr.vance@hospital.com", "+1 (555) 101-0005", "Room 101",
                BigDecimal.valueOf(50.00), "Mon, Tue, Wed, Thu, Fri, Sat", "09:00 AM - 06:00 PM"));

        // 3. User Accounts (BCrypt encoded)
        String defaultHashedPassword = passwordEncoder.encode("Password@123");

        User receptionistUser = userRepository.save(new User(
                "receptionist@hospital.com",
                defaultHashedPassword,
                "Jessica Hayes (Head Receptionist)",
                RoleType.ROLE_RECEPTIONIST
        ));

        User patientUser1 = userRepository.save(new User(
                "patient@hospital.com",
                defaultHashedPassword,
                "Sarah Jenkins",
                RoleType.ROLE_PATIENT
        ));

        User patientUser2 = userRepository.save(new User(
                "james.miller@hospital.com",
                defaultHashedPassword,
                "James Miller",
                RoleType.ROLE_PATIENT
        ));

        // 4. Patients
        Patient patient1 = patientRepository.save(new Patient(
                "PAT-2026-0001", patientUser1, "Sarah Jenkins", "patient@hospital.com",
                "+1 (555) 234-5678", LocalDate.of(1992, 5, 14), "FEMALE", "A+",
                "742 Evergreen Terrace, Springfield", "David Jenkins (Husband)",
                "+1 (555) 234-9999", "Penicillin, Peanuts", "Mild Asthma"
        ));

        Patient patient2 = patientRepository.save(new Patient(
                "PAT-2026-0002", patientUser2, "James Miller", "james.miller@hospital.com",
                "+1 (555) 876-5432", LocalDate.of(1985, 11, 23), "MALE", "O+",
                "124 Elm Street, Riverdale", "Laura Miller (Wife)",
                "+1 (555) 876-1111", "Sulfa drugs", "Hypertension"
        ));

        Patient patient3 = patientRepository.save(new Patient(
                "PAT-2026-0003", null, "Emily Davis", "emily.davis@email.com",
                "+1 (555) 345-6789", LocalDate.of(1998, 3, 30), "FEMALE", "B+",
                "450 Maple Avenue, Metropolis", "Thomas Davis (Father)",
                "+1 (555) 345-2222", "None known", "None"
        ));

        Patient patient4 = patientRepository.save(new Patient(
                "PAT-2026-0004", null, "Michael Johnson", "michael.j@email.com",
                "+1 (555) 456-7890", LocalDate.of(1979, 8, 12), "MALE", "AB-",
                "89 Oak Lane, Gotham", "Anna Johnson (Sister)",
                "+1 (555) 456-3333", "Aspirin", "Type 2 Diabetes"
        ));

        LocalDate today = LocalDate.now();

        // 5. Appointments
        // Today - Patient 1 (Checked In / In Queue)
        Appointment appt1 = appointmentRepository.save(new Appointment(
                patient1, drVance, generalMedicine, today,
                LocalTime.of(9, 30), "09:30 AM",
                "Follow-up for seasonal allergies and breathing difficulty",
                AppointmentStatus.CHECKED_IN
        ));

        // Today - Patient 2 (In Consultation / Queue Q-102)
        Appointment appt2 = appointmentRepository.save(new Appointment(
                patient2, drWright, cardiology, today,
                LocalTime.of(10, 0), "10:00 AM",
                "Routine ECG and Blood Pressure evaluation",
                AppointmentStatus.IN_CONSULTATION
        ));

        // Today - Patient 3 (Scheduled)
        Appointment appt3 = appointmentRepository.save(new Appointment(
                patient3, drGomez, neurology, today,
                LocalTime.of(11, 30), "11:30 AM",
                "Persistent tension headache and dizziness",
                AppointmentStatus.SCHEDULED
        ));

        // Today - Patient 4 (Scheduled)
        Appointment appt4 = appointmentRepository.save(new Appointment(
                patient4, drChen, pediatrics, today,
                LocalTime.of(14, 0), "02:00 PM",
                "Child vaccination and growth milestone review",
                AppointmentStatus.SCHEDULED
        ));

        // Past Appointment for Sarah Jenkins (Completed)
        Appointment pastAppt = appointmentRepository.save(new Appointment(
                patient1, drVance, generalMedicine, today.minusDays(14),
                LocalTime.of(10, 30), "10:30 AM",
                "Initial consultation for persistent cough and fatigue",
                AppointmentStatus.COMPLETED
        ));

        // Upcoming Appointment for Sarah Jenkins
        Appointment futureAppt = appointmentRepository.save(new Appointment(
                patient1, drWright, cardiology, today.plusDays(7),
                LocalTime.of(10, 0), "10:00 AM",
                "Cardiovascular health checkup and stress test",
                AppointmentStatus.SCHEDULED
        ));

        // 6. Queue Entries for Today
        QueueEntry q1 = queueEntryRepository.save(new QueueEntry(
                "Q-101", appt1, patient1, drVance, today, QueueStatus.WAITING
        ));
        q1.setCheckInTime(LocalDateTime.now().minusMinutes(25));
        queueEntryRepository.save(q1);

        QueueEntry q2 = queueEntryRepository.save(new QueueEntry(
                "Q-102", appt2, patient2, drWright, today, QueueStatus.IN_CONSULTATION
        ));
        q2.setCheckInTime(LocalDateTime.now().minusMinutes(45));
        q2.setCalledTime(LocalDateTime.now().minusMinutes(10));
        queueEntryRepository.save(q2);

        // 7. Medical Records for Sarah Jenkins
        medicalRecordRepository.save(new MedicalRecord(
                patient1, drVance, today.minusDays(14),
                "Persistent dry cough, mild fever (99.8 F), throat irritation, nocturnal wheezing.",
                "Acute Bronchitis with Mild Asthma Exacerbation",
                "Chest sounds revealed slight bilateral expiratory wheezing. No focal consolidation. Throat mildly erythematous. Advised rest, increased fluid intake, and prescribed oral bronchodilator.",
                "BP: 118/76 mmHg | Pulse: 78 bpm | Temp: 98.6 F | SpO2: 98%",
                "Follow up in 2 weeks or immediately if shortness of breath worsens."
        ));

        medicalRecordRepository.save(new MedicalRecord(
                patient1, drVance, today.minusMonths(6),
                "General fatigue and routine annual physical examination.",
                "Annual Wellness Exam - Generally Healthy",
                "Heart sounds normal S1 and S2. Lungs clear to auscultation bilaterally. Abdomen soft, non-tender. Advised routine blood screen and lipid panel.",
                "BP: 120/80 mmHg | Pulse: 72 bpm | Temp: 98.4 F | SpO2: 99%",
                "Continue balanced Mediterranean diet, maintain 150 minutes of weekly aerobic exercise."
        ));

        // 8. Prescriptions for Sarah Jenkins
        prescriptionRepository.save(new Prescription(
                patient1, drVance, today.minusDays(14),
                "Amoxicillin-Clavulanate (Augmentin)", "625 mg",
                "Twice daily after meals", "7 days",
                "Complete full 7-day course even if symptoms improve early. Take with water.",
                "Completed"
        ));

        prescriptionRepository.save(new Prescription(
                patient1, drVance, today.minusDays(14),
                "Albuterol Sulfate Inhalation Aerosol", "90 mcg/actuation",
                "1 to 2 puffs every 4-6 hours as needed", "30 days",
                "Rinse mouth after use. Keep rescue inhaler handy at all times.",
                "Active"
        ));

        prescriptionRepository.save(new Prescription(
                patient1, drVance, today.minusDays(14),
                "Cetirizine Hydrochloride (Zyrtec)", "10 mg",
                "Once daily in the evening", "30 days",
                "May cause mild drowsiness. Avoid alcohol while taking.",
                "Active"
        ));

        // 9. Lab Reports for Sarah Jenkins
        labReportRepository.save(new LabReport(
                patient1, drVance,
                "Complete Blood Count with Differential (CBC)",
                "Hematology",
                today.minusDays(14),
                today.minusDays(12),
                LabReportStatus.COMPLETED,
                "Hemoglobin: 14.1 g/dL | RBC: 4.65 M/mcL | WBC: 7,200 /mcL | Platelets: 260,000 /mcL | Neutrophils: 62% | Lymphocytes: 28%",
                "Hb: 12.0-15.5 g/dL | WBC: 4,500-11,000 /mcL",
                "All parameters within optimal physiological baseline. No sign of acute bacterial infection."
        ));

        labReportRepository.save(new LabReport(
                patient1, drWright,
                "Comprehensive Lipid Profile",
                "Biochemistry",
                today.minusMonths(2),
                today.minusMonths(2).plusDays(1),
                LabReportStatus.REVIEWED,
                "Total Cholesterol: 182 mg/dL | HDL ('Good'): 58 mg/dL | LDL ('Bad'): 104 mg/dL | Triglycerides: 100 mg/dL",
                "Cholesterol: <200 mg/dL | HDL: >50 mg/dL | LDL: <100 mg/dL",
                "Good cardiovascular lipid metrics. Maintain current diet and exercise habits."
        ));

        labReportRepository.save(new LabReport(
                patient1, drVance,
                "Thyroid Stimulating Hormone (TSH, 3rd Gen)",
                "Endocrinology",
                today.minusDays(2),
                null,
                LabReportStatus.PENDING,
                "Sample collected and processing in laboratory. Results expected within 24 hours.",
                "0.4 - 4.0 mIU/L",
                "Pending final clinical sign-off."
        ));

        // 10. Invoices for Sarah Jenkins
        invoiceRepository.save(new Invoice(
                "INV-2026-0001", patient1, today.minusDays(14), today.minusDays(14),
                "Outpatient Consultation & Vitals Assessment - Dr. Marcus Vance",
                BigDecimal.valueOf(50.00), BigDecimal.valueOf(2.50), BigDecimal.valueOf(0.00),
                BigDecimal.valueOf(52.50), PaymentStatus.PAID, "Credit Card (Visa ending in 4242)",
                today.minusDays(14)
        ));

        invoiceRepository.save(new Invoice(
                "INV-2026-0002", patient1, today.minusDays(2), today.plusDays(12),
                "Laboratory Diagnostics: TSH Panel + Serum Chemistry",
                BigDecimal.valueOf(75.00), BigDecimal.valueOf(3.75), BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(73.75), PaymentStatus.PENDING, null, null
        ));

        // 11. Notifications for Sarah Jenkins
        notificationRepository.save(new Notification(
                patientUser1,
                "Checked In - Queue Number Issued",
                "You are checked in! Your Queue Token is Q-101 for Dr. Marcus Vance (Room 101). Please take a seat in the waiting area.",
                "QUEUE_UPDATE"
        ));

        notificationRepository.save(new Notification(
                patientUser1,
                "Upcoming Appointment Reminder",
                "You have an upcoming appointment with Dr. Alexander Wright on " + today.plusDays(7) + " at 10:00 AM.",
                "APPOINTMENT_REMINDER"
        ));

        notificationRepository.save(new Notification(
                patientUser1,
                "Lab Report Available",
                "Your Complete Blood Count (CBC) report has been processed and is ready to view.",
                "LAB_READY"
        ));

        notificationRepository.save(new Notification(
                patientUser1,
                "Invoice Issued",
                "Invoice INV-2026-0002 for $73.75 has been generated and is due on " + today.plusDays(12) + ".",
                "INVOICE_GENERATED"
        ));
    }
}
