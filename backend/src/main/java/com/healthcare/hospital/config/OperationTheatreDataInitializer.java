package com.healthcare.hospital.config;

import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.*;
import com.healthcare.hospital.repository.*;
import com.healthcare.hospital.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Seeds standard Operation Theatres, equipment readiness, and sample clinical surgical workflows.
 */
@Component
@Order(10)
public class OperationTheatreDataInitializer implements CommandLineRunner {

    private final OperationTheatreRepository theatreRepository;
    private final SurgeryBookingRepository bookingRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final SurgeryWorkflowService workflowService;
    private final SurgeryBillingService billingService;
    private final SurgeryInventoryService inventoryService;
    private final SurgicalTeamMemberRepository teamMemberRepository;

    public OperationTheatreDataInitializer(OperationTheatreRepository theatreRepository,
                                           SurgeryBookingRepository bookingRepository,
                                           PatientRepository patientRepository,
                                           DoctorRepository doctorRepository,
                                           UserRepository userRepository,
                                           MedicineRepository medicineRepository,
                                           SurgeryWorkflowService workflowService,
                                           SurgeryBillingService billingService,
                                           SurgeryInventoryService inventoryService,
                                           SurgicalTeamMemberRepository teamMemberRepository) {
        this.theatreRepository = theatreRepository;
        this.bookingRepository = bookingRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.medicineRepository = medicineRepository;
        this.workflowService = workflowService;
        this.billingService = billingService;
        this.inventoryService = inventoryService;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (theatreRepository.count() > 0) {
            return;
        }

        // 1. Register Operation Theatres
        OperationTheatre ot1 = new OperationTheatre(
                "OT-01", "Main Cardiac & Thoracic Suite", "Building A, 3rd Floor, West Wing",
                "CARDIAC", 2,
                "Cardiopulmonary Heart-Lung Machine, Intra-aortic Balloon Pump, Advanced Hemodynamic Monitor, C-Arm Fluoroscope, Dual Anesthesia Workstation"
        );
        ot1.setStatus(TheatreStatus.AVAILABLE);
        ot1 = theatreRepository.save(ot1);

        OperationTheatre ot2 = new OperationTheatre(
                "OT-02", "General & Advanced Laparoscopy Suite", "Building A, 3rd Floor, Suite 2",
                "GENERAL", 2,
                "4K Ultra-HD Laparoscopic Tower, Harmonic Scalpel, Argon Plasma Coagulator, CO2 Insufflator, Electrosurgical Unit"
        );
        ot2.setStatus(TheatreStatus.AVAILABLE);
        ot2 = theatreRepository.save(ot2);

        OperationTheatre ot3 = new OperationTheatre(
                "OT-03", "Orthopedic & Trauma Theatre", "Building B, 2nd Floor, Wing C",
                "ORTHOPEDIC", 2,
                "Radiolucent Orthopedic Traction Table, High-Definition Mobile C-Arm, Pneumatic Surgical Drills, Arthroscopy System, Sterile Air Laminar Flow"
        );
        ot3.setStatus(TheatreStatus.AVAILABLE);
        ot3 = theatreRepository.save(ot3);

        OperationTheatre ot4 = new OperationTheatre(
                "OT-04", "Emergency & Rapid Response OT", "Building A, Ground Floor, Emergency Trauma Hub",
                "EMERGENCY", 1,
                "Rapid Blood/Fluid Infuser, Emergency Surgical Defibrillator, Video Laryngoscope, Portable Ultrasound, Dual Suction Regulators"
        );
        ot4.setStatus(TheatreStatus.AVAILABLE);
        ot4 = theatreRepository.save(ot4);

        // 2. Ensure existing Patient and Doctor exist before seeding surgeries
        List<Patient> patients = patientRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();
        List<User> users = userRepository.findAll();

        if (patients.isEmpty() || doctors.isEmpty()) {
            return;
        }

        Patient patient1 = patients.get(0);
        Patient patient2 = patients.size() > 1 ? patients.get(1) : patient1;
        Doctor doc1 = doctors.get(0);
        Doctor doc2 = doctors.size() > 1 ? doctors.get(1) : doc1;

        User nurseUser = users.stream()
                .filter(u -> u.getRole() == RoleType.ROLE_NURSE || u.getRole() == RoleType.ROLE_STAFF)
                .findFirst().orElse(null);

        LocalDate today = LocalDate.now();

        // 3. Seed Completed Surgery (Yesterday)
        SurgeryBooking surgCompleted = new SurgeryBooking(
                "SURG-2026-0001", patient1, ot1, doc1,
                "Coronary Artery Bypass Graft (CABG)", today.minusDays(1),
                LocalTime.of(8, 30), 180, SurgeryPriority.ROUTINE, "admin@hospital.com"
        );
        surgCompleted.setProcedureCategory("Cardiac");
        surgCompleted.setClinicalIndication("Severe triple vessel coronary artery disease with exertional angina");
        surgCompleted.setPreoperativeDiagnosis("Triple Vessel CAD with left main stenosis");
        surgCompleted.setStatus(SurgeryStatus.COMPLETED);
        surgCompleted.setActualStartTime(LocalDateTime.of(today.minusDays(1), LocalTime.of(8, 35)));
        surgCompleted.setActualEndTime(LocalDateTime.of(today.minusDays(1), LocalTime.of(11, 40)));
        surgCompleted.setPostoperativeDiagnosis("Triple vessel coronary disease; Successful 3-vessel bypass performed");
        surgCompleted.setOperativeNotes("Patient brought into OT-01 in stable condition. Median sternotomy performed. LIMA harvested and anastomosed to LAD; saphenous vein grafts to OM1 and RCA. Uneventful weaning off cardiopulmonary bypass. Hemostasis achieved. Chest tubes placed and closed in layers.");
        surgCompleted.setAnesthesiaNotes("General endotracheal anesthesia. Propofol, fentanyl, rocuronium induction. Sevoflurane maintenance. Continuous arterial line and central venous monitoring.");
        surgCompleted.setRecoveryDestination("Cardiothoracic ICU - Bed C-04");
        surgCompleted = bookingRepository.save(surgCompleted);

        // Add team members
        teamMemberRepository.save(new SurgicalTeamMember(surgCompleted, doc1, "LEAD_SURGEON", "admin@hospital.com"));
        if (doc2 != null && !doc2.getId().equals(doc1.getId())) {
            teamMemberRepository.save(new SurgicalTeamMember(surgCompleted, doc2, "ASSISTANT_SURGEON", "admin@hospital.com"));
        }
        if (nurseUser != null) {
            teamMemberRepository.save(new SurgicalTeamMember(surgCompleted, nurseUser, "SCRUB_NURSE", "admin@hospital.com"));
        }

        // Initialize and complete checklists for completed surgery
        workflowService.initializeChecklistForSurgery(surgCompleted);
        List<SurgeryChecklistItem> items = workflowService.getChecklistItems(surgCompleted);
        for (SurgeryChecklistItem item : items) {
            item.setStatus(ChecklistStatus.COMPLETED);
            item.setCompletedBy("admin@hospital.com");
            item.setCompletedAt(LocalDateTime.of(today.minusDays(1), LocalTime.of(8, 45)));
        }

        // Generate billing invoice for completed surgery
        try {
            billingService.generateSurgeryInvoice(surgCompleted.getId(), "admin@hospital.com");
        } catch (Exception ignored) {
        }

        // 4. Seed Scheduled Routine Surgery for Today
        SurgeryBooking surgToday = new SurgeryBooking(
                "SURG-2026-0002", patient2, ot2, doc2,
                "Laparoscopic Cholecystectomy", today,
                LocalTime.of(14, 0), 90, SurgeryPriority.ROUTINE, "admin@hospital.com"
        );
        surgToday.setProcedureCategory("General");
        surgToday.setClinicalIndication("Symptomatic cholelithiasis with recurrent biliary colic");
        surgToday.setPreoperativeDiagnosis("Chronic cholecystitis with gallstones");
        surgToday.setStatus(SurgeryStatus.SCHEDULED);
        surgToday = bookingRepository.save(surgToday);

        teamMemberRepository.save(new SurgicalTeamMember(surgToday, doc2, "LEAD_SURGEON", "admin@hospital.com"));
        if (nurseUser != null) {
            teamMemberRepository.save(new SurgicalTeamMember(surgToday, nurseUser, "CIRCULATING_NURSE", "admin@hospital.com"));
        }
        workflowService.initializeChecklistForSurgery(surgToday);

        // 5. Seed Urgent Orthopedic Surgery for Tomorrow
        SurgeryBooking surgTomorrow = new SurgeryBooking(
                "SURG-2026-0003", patient1, ot3, doc1,
                "Open Reduction and Internal Fixation (ORIF) Femur", today.plusDays(1),
                LocalTime.of(10, 0), 120, SurgeryPriority.URGENT, "admin@hospital.com"
        );
        surgTomorrow.setProcedureCategory("Orthopedic");
        surgTomorrow.setClinicalIndication("Displaced subtrochanteric fracture right femur following trauma");
        surgTomorrow.setPreoperativeDiagnosis("Closed fracture right proximal femur");
        surgTomorrow.setStatus(SurgeryStatus.SCHEDULED);
        surgTomorrow = bookingRepository.save(surgTomorrow);

        teamMemberRepository.save(new SurgicalTeamMember(surgTomorrow, doc1, "LEAD_SURGEON", "admin@hospital.com"));
        workflowService.initializeChecklistForSurgery(surgTomorrow);
    }
}
