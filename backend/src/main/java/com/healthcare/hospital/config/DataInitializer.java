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
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final PasswordEncoder passwordEncoder;

    // Pharmacy Repositories
    private final MedicineCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockTransactionRepository transactionRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsReceivedNoteRepository grnRepository;
    private final PrescriptionDispensationRepository dispensationRepository;
    private final PharmacyAuditLogRepository auditLogRepository;

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
                           DoctorAvailabilityRepository doctorAvailabilityRepository,
                           PasswordEncoder passwordEncoder,
                           MedicineCategoryRepository categoryRepository,
                           SupplierRepository supplierRepository,
                           MedicineRepository medicineRepository,
                           MedicineBatchRepository batchRepository,
                           StockTransactionRepository transactionRepository,
                           PurchaseOrderRepository purchaseOrderRepository,
                           GoodsReceivedNoteRepository grnRepository,
                           PrescriptionDispensationRepository dispensationRepository,
                           PharmacyAuditLogRepository auditLogRepository) {
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
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.grnRepository = grnRepository;
        this.dispensationRepository = dispensationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) {
        String defaultHashedPassword = passwordEncoder.encode("Password@123");

        if (userRepository.count() > 0) {
            seedDoctorUsersAndAvailabilityIfMissing();
            seedPharmacyDataIfMissing(defaultHashedPassword);
            return;
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

        // 3. User Accounts
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

        // Create and link doctor user accounts
        attachDoctorUser(drWright, "dr.wright@hospital.com", "Dr. Alexander Wright", defaultHashedPassword);
        attachDoctorUser(drGomez, "dr.gomez@hospital.com", "Dr. Elena Gomez", defaultHashedPassword);
        attachDoctorUser(drChen, "dr.chen@hospital.com", "Dr. Robert Chen", defaultHashedPassword);
        attachDoctorUser(drPatel, "dr.patel@hospital.com", "Dr. Sarah Patel", defaultHashedPassword);
        attachDoctorUser(drVance, "dr.vance@hospital.com", "Dr. Marcus Vance", defaultHashedPassword);

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
        Appointment appt1 = appointmentRepository.save(new Appointment(
                patient1, drVance, generalMedicine, today,
                LocalTime.of(9, 30), "09:30 AM",
                "Follow-up for seasonal allergies and breathing difficulty",
                AppointmentStatus.CHECKED_IN
        ));

        Appointment appt2 = appointmentRepository.save(new Appointment(
                patient2, drWright, cardiology, today,
                LocalTime.of(10, 0), "10:00 AM",
                "Routine ECG and Blood Pressure evaluation",
                AppointmentStatus.IN_CONSULTATION
        ));

        Appointment appt3 = appointmentRepository.save(new Appointment(
                patient3, drGomez, neurology, today,
                LocalTime.of(11, 30), "11:30 AM",
                "Persistent tension headache and dizziness",
                AppointmentStatus.SCHEDULED
        ));

        Appointment appt4 = appointmentRepository.save(new Appointment(
                patient4, drChen, pediatrics, today,
                LocalTime.of(14, 0), "02:00 PM",
                "Child vaccination and growth milestone review",
                AppointmentStatus.SCHEDULED
        ));

        Appointment pastAppt = appointmentRepository.save(new Appointment(
                patient1, drVance, generalMedicine, today.minusDays(14),
                LocalTime.of(10, 30), "10:30 AM",
                "Initial consultation for persistent cough and fatigue",
                AppointmentStatus.COMPLETED
        ));

        Appointment futureAppt = appointmentRepository.save(new Appointment(
                patient1, drWright, cardiology, today.plusDays(7),
                LocalTime.of(10, 0), "10:00 AM",
                "Cardiovascular health checkup and stress test",
                AppointmentStatus.SCHEDULED
        ));

        // 6. Queue Entries
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

        // 7. Medical Records
        MedicalRecord mr1 = medicalRecordRepository.save(new MedicalRecord(
                patient1, drVance, pastAppt, today.minusDays(14),
                "Persistent dry cough, mild fever (99.8 F), throat irritation, nocturnal wheezing.",
                "Acute Bronchitis with Mild Asthma Exacerbation",
                "Chest sounds revealed slight bilateral expiratory wheezing. No focal consolidation. Throat mildly erythematous. Advised rest, increased fluid intake, and prescribed oral bronchodilator.",
                "BP: 118/76 mmHg | Pulse: 78 bpm | Temp: 98.6 F | SpO2: 98%",
                "Follow up in 2 weeks or immediately if shortness of breath worsens."
        ));

        medicalRecordRepository.save(new MedicalRecord(
                patient1, drVance, null, today.minusMonths(6),
                "General fatigue and routine annual physical examination.",
                "Annual Wellness Exam - Generally Healthy",
                "Heart sounds normal S1 and S2. Lungs clear to auscultation bilaterally. Abdomen soft, non-tender. Advised routine blood screen and lipid panel.",
                "BP: 120/80 mmHg | Pulse: 72 bpm | Temp: 98.4 F | SpO2: 99%",
                "Continue balanced Mediterranean diet, maintain 150 minutes of weekly aerobic exercise."
        ));

        // 8. Prescriptions
        Prescription rx1 = prescriptionRepository.save(new Prescription(
                patient1, drVance, pastAppt, mr1, today.minusDays(14),
                "Amoxicillin-Clavulanate (Augmentin)", "625 mg",
                "Twice daily after meals", "7 days",
                "Complete full 7-day course even if symptoms improve early. Take with water.",
                "Completed"
        ));
        rx1.setPrescribedQuantity(14);
        rx1.setDispensedQuantity(14);
        rx1.setDispenseStatus("DISPENSED");
        prescriptionRepository.save(rx1);

        Prescription rx2 = prescriptionRepository.save(new Prescription(
                patient1, drVance, pastAppt, mr1, today.minusDays(14),
                "Albuterol Sulfate Inhalation Aerosol", "90 mcg/actuation",
                "1 to 2 puffs every 4-6 hours as needed", "30 days",
                "Rinse mouth after use. Keep rescue inhaler handy at all times.",
                "Active"
        ));
        rx2.setPrescribedQuantity(2);
        rx2.setDispensedQuantity(0);
        rx2.setDispenseStatus("PENDING");
        prescriptionRepository.save(rx2);

        Prescription rx3 = prescriptionRepository.save(new Prescription(
                patient1, drVance, pastAppt, mr1, today.minusDays(14),
                "Cetirizine Hydrochloride (Zyrtec)", "10 mg",
                "Once daily in the evening", "30 days",
                "May cause mild drowsiness. Avoid alcohol while taking.",
                "Active"
        ));
        rx3.setPrescribedQuantity(30);
        rx3.setDispensedQuantity(10);
        rx3.setDispenseStatus("PARTIALLY_DISPENSED");
        prescriptionRepository.save(rx3);

        Prescription rx4 = prescriptionRepository.save(new Prescription(
                patient2, drWright, appt2, null, today,
                "Atorvastatin Calcium", "20 mg",
                "Once daily at bedtime", "30 days",
                "Take with or without food. Avoid grapefruit juice.",
                "Active"
        ));
        rx4.setPrescribedQuantity(30);
        rx4.setDispensedQuantity(0);
        rx4.setDispenseStatus("PENDING");
        prescriptionRepository.save(rx4);

        // 9. Lab Reports
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

        // 10. Invoices
        invoiceRepository.save(new Invoice(
                "INV-2026-0001", patient1, today.minusDays(14), today.minusDays(14),
                "Outpatient Consultation & Vitals Assessment - Dr. Marcus Vance",
                BigDecimal.valueOf(50.00), BigDecimal.valueOf(2.50), BigDecimal.valueOf(0.00),
                BigDecimal.valueOf(52.50), PaymentStatus.PAID, "Credit Card (Visa ending in 4242)",
                today.minusDays(14)
        ));

        // 11. Notifications
        notificationRepository.save(new Notification(
                patientUser1,
                "Checked In - Queue Number Issued",
                "You are checked in! Your Queue Token is Q-101 for Dr. Marcus Vance (Room 101). Please take a seat in the waiting area.",
                "QUEUE_UPDATE"
        ));

        // 12. Doctor Availability Slots
        seedSampleAvailability(drVance, drWright, today);

        // 13. Seed Pharmacy Data
        seedPharmacyDataIfMissing(defaultHashedPassword);
    }

    private void attachDoctorUser(Doctor doctor, String email, String fullName, String encodedPassword) {
        User user = userRepository.findByUsername(email).orElseGet(() ->
                userRepository.save(new User(email, encodedPassword, fullName, RoleType.ROLE_DOCTOR))
        );
        doctor.setUser(user);
        doctorRepository.save(doctor);
    }

    private void seedSampleAvailability(Doctor drVance, Doctor drWright, LocalDate today) {
        doctorAvailabilityRepository.save(new DoctorAvailability(
                drVance, today, LocalTime.of(9, 0), LocalTime.of(13, 0), "AVAILABLE", "Morning Clinic"
        ));
        doctorAvailabilityRepository.save(new DoctorAvailability(
                drVance, today, LocalTime.of(14, 0), LocalTime.of(17, 30), "AVAILABLE", "Afternoon Consultation"
        ));
        doctorAvailabilityRepository.save(new DoctorAvailability(
                drVance, today.plusDays(1), LocalTime.of(9, 0), LocalTime.of(14, 0), "AVAILABLE", "Regular OPD"
        ));

        doctorAvailabilityRepository.save(new DoctorAvailability(
                drWright, today, LocalTime.of(9, 30), LocalTime.of(16, 0), "AVAILABLE", "Cardiology Clinic"
        ));
        doctorAvailabilityRepository.save(new DoctorAvailability(
                drWright, today.plusDays(1), LocalTime.of(10, 0), LocalTime.of(15, 0), "AVAILABLE", "OPD & ECG review"
        ));
    }

    private void seedDoctorUsersAndAvailabilityIfMissing() {
        String defaultHashedPassword = passwordEncoder.encode("Password@123");
        List<Doctor> doctors = doctorRepository.findAll();
        for (Doctor doctor : doctors) {
            if (doctor.getUser() == null && doctor.getEmail() != null) {
                User user = userRepository.findByUsername(doctor.getEmail()).orElseGet(() ->
                        userRepository.save(new User(
                                doctor.getEmail(),
                                defaultHashedPassword,
                                "Dr. " + doctor.getFullName(),
                                RoleType.ROLE_DOCTOR
                        ))
                );
                doctor.setUser(user);
                doctorRepository.save(doctor);
            }
        }

        if (doctorAvailabilityRepository.count() == 0 && !doctors.isEmpty()) {
            Doctor firstDoc = doctors.get(0);
            Doctor secondDoc = doctors.size() > 1 ? doctors.get(1) : firstDoc;
            seedSampleAvailability(firstDoc, secondDoc, LocalDate.now());
        }
    }

    public void seedPharmacyDataIfMissing(String defaultHashedPassword) {
        // Seed Pharmacist User
        userRepository.findByUsername("pharmacist@hospital.com").orElseGet(() ->
                userRepository.save(new User(
                        "pharmacist@hospital.com",
                        defaultHashedPassword,
                        "Marcus Thorne (Chief Pharmacist)",
                        RoleType.ROLE_PHARMACIST
                ))
        );

        if (categoryRepository.count() > 0) {
            return; // Pharmacy data already seeded
        }

        LocalDate today = LocalDate.now();

        // 1. Medicine Categories
        MedicineCategory catAntibiotics = categoryRepository.save(new MedicineCategory("CAT-ANTI", "Antibiotics", "Antimicrobial medications for bacterial infections"));
        MedicineCategory catAnalgesics = categoryRepository.save(new MedicineCategory("CAT-ANAL", "Analgesics & Antipyretics", "Pain relief and fever reducers"));
        MedicineCategory catCardio = categoryRepository.save(new MedicineCategory("CAT-CARD", "Cardiovascular", "Heart, blood pressure and lipid lowering medications"));
        MedicineCategory catRespiratory = categoryRepository.save(new MedicineCategory("CAT-RESP", "Respiratory", "Bronchodilators, inhalers, and antihistamines"));
        MedicineCategory catDiabetic = categoryRepository.save(new MedicineCategory("CAT-DIAB", "Antidiabetic", "Blood sugar management and insulin sensitizers"));
        MedicineCategory catGastro = categoryRepository.save(new MedicineCategory("CAT-GAST", "Gastrointestinal", "Antacids, proton pump inhibitors, and antiemetics"));

        // 2. Suppliers
        Supplier supApex = supplierRepository.save(new Supplier(
                "SUP-001", "Apex Pharma Global", "David Vance", "orders@apexpharma.com",
                "+1 (555) 300-1100", "100 Industrial Parkway, Chicago, IL", "FDA-IL-98421", "Net 30"
        ));

        Supplier supMedLife = supplierRepository.save(new Supplier(
                "SUP-002", "MedLife Healthcare Distributors", "Karen Miller", "supply@medlife.org",
                "+1 (555) 300-2200", "450 Logistics Way, Indianapolis, IN", "FDA-IN-77112", "Net 15"
        ));

        Supplier supBioHealth = supplierRepository.save(new Supplier(
                "SUP-003", "BioHealth Solutions", "Robert Ross", "contact@biohealth.com",
                "+1 (555) 300-3300", "78 Science Park, Boston, MA", "FDA-MA-45678", "Cash on Delivery"
        ));

        // 3. Medicines
        Medicine medAmox = medicineRepository.save(new Medicine(
                "MED-0001", "Amoxicillin-Clavulanate (Augmentin)", "Amoxicillin / Clavulanate Potassium",
                catAntibiotics, "Tablet", "625 mg", "Strip", 25, BigDecimal.valueOf(18.50), BigDecimal.valueOf(11.20), "GlaxoSmithKline"
        ));

        Medicine medAlbuterol = medicineRepository.save(new Medicine(
                "MED-0002", "Albuterol Sulfate Inhalation Aerosol", "Albuterol Sulfate",
                catRespiratory, "Inhaler", "90 mcg/actuation", "Inhaler", 15, BigDecimal.valueOf(35.00), BigDecimal.valueOf(22.00), "Teva Pharmaceuticals"
        ));

        Medicine medCetirizine = medicineRepository.save(new Medicine(
                "MED-0003", "Cetirizine Hydrochloride (Zyrtec)", "Cetirizine HCl",
                catRespiratory, "Tablet", "10 mg", "Strip", 30, BigDecimal.valueOf(12.00), BigDecimal.valueOf(6.50), "Johnson & Johnson"
        ));

        Medicine medAtorvastatin = medicineRepository.save(new Medicine(
                "MED-0004", "Atorvastatin Calcium", "Atorvastatin",
                catCardio, "Tablet", "20 mg", "Box", 20, BigDecimal.valueOf(24.00), BigDecimal.valueOf(14.00), "Pfizer"
        ));

        Medicine medMetformin = medicineRepository.save(new Medicine(
                "MED-0005", "Metformin Hydrochloride", "Metformin HCl",
                catDiabetic, "Tablet", "500 mg", "Strip", 40, BigDecimal.valueOf(9.00), BigDecimal.valueOf(4.20), "Merck"
        ));

        Medicine medOmeprazole = medicineRepository.save(new Medicine(
                "MED-0006", "Omeprazole Delayed-Release (Prilosec)", "Omeprazole",
                catGastro, "Capsule", "20 mg", "Box", 25, BigDecimal.valueOf(15.00), BigDecimal.valueOf(8.50), "AstraZeneca"
        ));

        Medicine medParacetamol = medicineRepository.save(new Medicine(
                "MED-0007", "Paracetamol Extra Strength", "Acetaminophen",
                catAnalgesics, "Tablet", "500 mg", "Strip", 50, BigDecimal.valueOf(5.50), BigDecimal.valueOf(2.10), "Novartis"
        ));

        Medicine medCiprofloxacin = medicineRepository.save(new Medicine(
                "MED-0008", "Ciprofloxacin Ophthalmic Solution", "Ciprofloxacin",
                catAntibiotics, "Drops", "0.3%", "Bottle", 10, BigDecimal.valueOf(22.00), BigDecimal.valueOf(12.50), "Bayer"
        ));

        // 4. Batches (including active, low-stock, near-expiry, and expired)
        // Augmentin batches:
        MedicineBatch bAug1 = batchRepository.save(new MedicineBatch(
                "BAT-AUG-26A", medAmox, supApex, 100, BigDecimal.valueOf(11.20), BigDecimal.valueOf(18.50),
                today.minusMonths(2), today.plusMonths(14)
        ));
        bAug1.setCurrentQuantity(75);
        batchRepository.save(bAug1);

        MedicineBatch bAug2 = batchRepository.save(new MedicineBatch(
                "BAT-AUG-26B", medAmox, supApex, 50, BigDecimal.valueOf(11.20), BigDecimal.valueOf(18.50),
                today.minusMonths(1), today.plusMonths(18)
        ));
        bAug2.setCurrentQuantity(50);
        batchRepository.save(bAug2);

        // Albuterol (Low stock!)
        MedicineBatch bAlb1 = batchRepository.save(new MedicineBatch(
                "BAT-ALB-25A", medAlbuterol, supBioHealth, 30, BigDecimal.valueOf(22.00), BigDecimal.valueOf(35.00),
                today.minusMonths(6), today.plusMonths(8)
        ));
        bAlb1.setCurrentQuantity(8); // Below reorder level 15
        batchRepository.save(bAlb1);

        // Cetirizine (Near-expiry batch: expires in 25 days!)
        MedicineBatch bCet1 = batchRepository.save(new MedicineBatch(
                "BAT-CET-24X", medCetirizine, supMedLife, 80, BigDecimal.valueOf(6.50), BigDecimal.valueOf(12.00),
                today.minusMonths(11), today.plusDays(25) // Near expiry
        ));
        bCet1.setCurrentQuantity(35);
        batchRepository.save(bCet1);

        MedicineBatch bCet2 = batchRepository.save(new MedicineBatch(
                "BAT-CET-26A", medCetirizine, supMedLife, 150, BigDecimal.valueOf(6.50), BigDecimal.valueOf(12.00),
                today.minusMonths(1), today.plusMonths(24)
        ));
        bCet2.setCurrentQuantity(150);
        batchRepository.save(bCet2);

        // Atorvastatin (Expired batch: expired 15 days ago for realistic alert testing!)
        MedicineBatch bAtorExp = batchRepository.save(new MedicineBatch(
                "BAT-ATOR-23OLD", medAtorvastatin, supApex, 40, BigDecimal.valueOf(14.00), BigDecimal.valueOf(24.00),
                today.minusMonths(24), today.minusDays(15) // EXPIRED!
        ));
        bAtorExp.setCurrentQuantity(12);
        bAtorExp.setStatus(BatchStatus.EXPIRED);
        batchRepository.save(bAtorExp);

        MedicineBatch bAtorGood = batchRepository.save(new MedicineBatch(
                "BAT-ATOR-26A", medAtorvastatin, supApex, 100, BigDecimal.valueOf(14.00), BigDecimal.valueOf(24.00),
                today.minusDays(20), today.plusMonths(20)
        ));
        bAtorGood.setCurrentQuantity(85);
        batchRepository.save(bAtorGood);

        // Metformin (Good stock)
        MedicineBatch bMet1 = batchRepository.save(new MedicineBatch(
                "BAT-MET-26A", medMetformin, supMedLife, 200, BigDecimal.valueOf(4.20), BigDecimal.valueOf(9.00),
                today.minusDays(40), today.plusMonths(30)
        ));
        bMet1.setCurrentQuantity(180);
        batchRepository.save(bMet1);

        // Omeprazole (Good stock)
        MedicineBatch bOme1 = batchRepository.save(new MedicineBatch(
                "BAT-OME-26A", medOmeprazole, supBioHealth, 120, BigDecimal.valueOf(8.50), BigDecimal.valueOf(15.00),
                today.minusDays(15), today.plusMonths(16)
        ));
        bOme1.setCurrentQuantity(110);
        batchRepository.save(bOme1);

        // Paracetamol (Ample stock)
        MedicineBatch bPara1 = batchRepository.save(new MedicineBatch(
                "BAT-PARA-26A", medParacetamol, supMedLife, 300, BigDecimal.valueOf(2.10), BigDecimal.valueOf(5.50),
                today.minusDays(10), today.plusMonths(36)
        ));
        bPara1.setCurrentQuantity(280);
        batchRepository.save(bPara1);

        // Ciprofloxacin (Out of stock: 0 quantity!)
        MedicineBatch bCipro = batchRepository.save(new MedicineBatch(
                "BAT-CIP-25X", medCiprofloxacin, supApex, 20, BigDecimal.valueOf(12.50), BigDecimal.valueOf(22.00),
                today.minusMonths(8), today.plusMonths(4)
        ));
        bCipro.setCurrentQuantity(0);
        bCipro.setStatus(BatchStatus.DEPLETED);
        batchRepository.save(bCipro);

        // Refresh aggregate stock on all medicines
        updateMedicineStock(medAmox);
        updateMedicineStock(medAlbuterol);
        updateMedicineStock(medCetirizine);
        updateMedicineStock(medAtorvastatin);
        updateMedicineStock(medMetformin);
        updateMedicineStock(medOmeprazole);
        updateMedicineStock(medParacetamol);
        updateMedicineStock(medCiprofloxacin);

        // 5. Initial Stock Transactions
        transactionRepository.save(new StockTransaction(
                "STX-2026-0001", StockTransactionType.STOCK_IN, medAmox, bAug1, 100,
                bAug1.getPurchasePrice(), 0, 100, "INITIAL_STOCK", bAug1.getBatchNumber(),
                "pharmacist@hospital.com", "Opening stock intake"
        ));
        transactionRepository.save(new StockTransaction(
                "STX-2026-0002", StockTransactionType.DISPENSED, medAmox, bAug1, 25,
                bAug1.getUnitPrice(), 100, 75, "PRESCRIPTION", "DISP-2026-00001",
                "pharmacist@hospital.com", "Dispensed to outpatient"
        ));
        transactionRepository.save(new StockTransaction(
                "STX-2026-0003", StockTransactionType.STOCK_IN, medCetirizine, bCet1, 80,
                bCet1.getPurchasePrice(), 0, 80, "INITIAL_STOCK", bCet1.getBatchNumber(),
                "pharmacist@hospital.com", "Opening stock intake"
        ));
        transactionRepository.save(new StockTransaction(
                "STX-2026-0004", StockTransactionType.STOCK_IN, medAtorvastatin, bAtorGood, 100,
                bAtorGood.getPurchasePrice(), 0, 100, "INITIAL_STOCK", bAtorGood.getBatchNumber(),
                "pharmacist@hospital.com", "Bulk shipment receipt"
        ));

        // 6. Seed Sample Purchase Order
        PurchaseOrder po = new PurchaseOrder(
                "PO-2026-0001", supApex, today.minusDays(10), today.plusDays(5),
                "Restock critical cardiovascular and antibiotic lines", "pharmacist@hospital.com"
        );
        po.addItem(new PurchaseOrderItem(po, medAmox, 50, BigDecimal.valueOf(11.20)));
        po.addItem(new PurchaseOrderItem(po, medAtorvastatin, 50, BigDecimal.valueOf(14.00)));
        purchaseOrderRepository.save(po);

        // 7. Audit Log
        auditLogRepository.save(new PharmacyAuditLog(
                "SYSTEM_INITIALIZATION", "CareFlow HMS", "PHARMACY_M3",
                "pharmacist@hospital.com", "M3 Pharmacy subsystem initialized with demo catalogs and inventory.",
                "127.0.0.1"
        ));
    }

    private void updateMedicineStock(Medicine medicine) {
        int validStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
        medicine.setTotalStock(validStock);
        medicineRepository.save(medicine);
    }
}
