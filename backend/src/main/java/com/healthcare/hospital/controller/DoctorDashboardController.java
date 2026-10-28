package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.*;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.enums.QueueStatus;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.PatientRepository;
import com.healthcare.hospital.repository.QueueEntryRepository;
import com.healthcare.hospital.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorDashboardController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationService consultationService;
    private final DoctorAvailabilityService availabilityService;
    private final MedicalService medicalService;
    private final PatientRepository patientRepository;
    private final QueueEntryRepository queueEntryRepository;

    public DoctorDashboardController(DoctorService doctorService,
                                     AppointmentService appointmentService,
                                     AppointmentRepository appointmentRepository,
                                     ConsultationService consultationService,
                                     DoctorAvailabilityService availabilityService,
                                     MedicalService medicalService,
                                     PatientRepository patientRepository,
                                     QueueEntryRepository queueEntryRepository) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.consultationService = consultationService;
        this.availabilityService = availabilityService;
        this.medicalService = medicalService;
        this.patientRepository = patientRepository;
        this.queueEntryRepository = queueEntryRepository;
    }

    private Doctor getAuthenticatedDoctor(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("Authentication required");
        }
        return doctorService.getDoctorByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found for user: " + userDetails.getUsername()));
    }

    // 1. Dashboard Overview
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        LocalDate today = LocalDate.now();

        List<Appointment> todayAppointments = appointmentRepository.findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(doctor, today);
        List<Appointment> upcomingAppointments = appointmentRepository.findByDoctorAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(doctor, today.plusDays(1));

        long todayTotal = todayAppointments.size();
        long completedToday = appointmentRepository.countByDoctorAndAppointmentDateAndStatus(doctor, today, AppointmentStatus.COMPLETED);
        long pendingConsultations = todayAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN || a.getStatus() == AppointmentStatus.SCHEDULED || a.getStatus() == AppointmentStatus.IN_CONSULTATION)
                .count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("todayTotal", todayTotal);
        model.addAttribute("completedToday", completedToday);
        model.addAttribute("pendingConsultations", pendingConsultations);
        model.addAttribute("upcomingTotal", upcomingAppointments.size());

        return "doctor/dashboard";
    }

    // 2. Appointment Management
    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @ModelAttribute("filter") DoctorAppointmentFilterDto filter,
                               Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);

        List<Appointment> appointments;
        if ((filter.getDate() != null) || (filter.getStatus() != null) || (filter.getKeyword() != null && !filter.getKeyword().isBlank())) {
            appointments = appointmentRepository.filterDoctorAppointments(
                    doctor,
                    filter.getDate(),
                    filter.getStatus(),
                    filter.getKeyword() != null ? filter.getKeyword().trim() : null
            );
        } else {
            appointments = appointmentRepository.findByDoctorOrderByAppointmentDateDescAppointmentTimeAsc(doctor);
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("statuses", AppointmentStatus.values());

        return "doctor/appointments";
    }

    // Update appointment status
    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable("id") Long id,
                                          @RequestParam("status") AppointmentStatus status,
                                          @RequestParam(value = "cancellationReason", required = false) String cancellationReason,
                                          RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));

        // Enforce ownership
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Unauthorized access to appointment.");
        }

        appointment.setStatus(status);
        if (status == AppointmentStatus.CANCELLED) {
            appointment.setCancellationReason(cancellationReason != null && !cancellationReason.isBlank() ? cancellationReason : "Cancelled by Doctor");
        }
        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated to " + status.getDisplayName());
        return "redirect:/doctor/appointments";
    }

    // 3. Patient Medical History
    @GetMapping("/patients")
    public String patientsDirectory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam(value = "search", required = false) String search,
                                    Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        List<Patient> patients = appointmentRepository.findDistinctPatientsByDoctor(doctor);

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase().trim();
            patients = patients.stream()
                    .filter(p -> p.getFullName().toLowerCase().contains(q) || p.getPatientId().toLowerCase().contains(q))
                    .toList();
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("patients", patients);
        model.addAttribute("searchQuery", search);

        return "doctor/patients";
    }

    @GetMapping("/patients/{id}")
    public String patientMedicalHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @PathVariable("id") Long patientId,
                                        Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        // Fetch patient's full clinical history
        List<MedicalRecord> records = medicalService.getMedicalRecords(patient);
        List<Prescription> prescriptions = medicalService.getPrescriptions(patient);
        List<LabReport> labReports = medicalService.getLabReports(patient);
        List<Appointment> pastAppointments = appointmentRepository.findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(patient);

        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecords", records);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("labReports", labReports);
        model.addAttribute("pastAppointments", pastAppointments);

        return "doctor/patient-history";
    }

    // 4. Consultation & Diagnosis Management
    @GetMapping("/consultations")
    public String consultationsHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        List<MedicalRecord> consultations = consultationService.getDoctorConsultations(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("consultations", consultations);

        return "doctor/consultations";
    }

    @GetMapping("/consultations/start")
    public String startConsultationSession(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam("appointmentId") Long appointmentId,
                                           Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Unauthorized: This appointment is not assigned to you.");
        }

        Patient patient = appointment.getPatient();
        List<MedicalRecord> pastRecords = medicalService.getMedicalRecords(patient);
        List<Prescription> pastPrescriptions = medicalService.getPrescriptions(patient);
        List<LabReport> labReports = medicalService.getLabReports(patient);

        ConsultationDto consultationDto = new ConsultationDto();
        consultationDto.setAppointmentId(appointmentId);
        consultationDto.setPatientId(patient.getId());

        // Check if consultation notes already exist for this appointment
        Optional<MedicalRecord> existingRecord = consultationService.getConsultationForAppointment(appointment);
        if (existingRecord.isPresent()) {
            MedicalRecord record = existingRecord.get();
            consultationDto.setSymptoms(record.getSymptoms());
            consultationDto.setVitalSigns(record.getVitalSigns());
            consultationDto.setDiagnosis(record.getDiagnosis());
            consultationDto.setClinicalNotes(record.getClinicalNotes());
            consultationDto.setFollowUpInstructions(record.getFollowUpInstructions());
        }

        // If appointment was scheduled or checked in, move to IN_CONSULTATION
        if (appointment.getStatus() == AppointmentStatus.SCHEDULED || appointment.getStatus() == AppointmentStatus.CHECKED_IN) {
            appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
            appointmentRepository.save(appointment);

            Optional<QueueEntry> queueEntryOpt = queueEntryRepository.findByAppointment(appointment);
            if (queueEntryOpt.isPresent()) {
                QueueEntry qe = queueEntryOpt.get();
                qe.setStatus(QueueStatus.IN_CONSULTATION);
                queueEntryRepository.save(qe);
            }
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", patient);
        model.addAttribute("pastRecords", pastRecords);
        model.addAttribute("pastPrescriptions", pastPrescriptions);
        model.addAttribute("labReports", labReports);
        model.addAttribute("consultationDto", consultationDto);

        return "doctor/consultation-form";
    }

    @PostMapping("/consultations/save")
    public String saveConsultationSession(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @Valid @ModelAttribute("consultationDto") ConsultationDto dto,
                                          BindingResult bindingResult,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);

        if (bindingResult.hasErrors()) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
            Patient patient = appointment.getPatient();
            model.addAttribute("doctor", doctor);
            model.addAttribute("appointment", appointment);
            model.addAttribute("patient", patient);
            model.addAttribute("pastRecords", medicalService.getMedicalRecords(patient));
            model.addAttribute("pastPrescriptions", medicalService.getPrescriptions(patient));
            model.addAttribute("labReports", medicalService.getLabReports(patient));
            return "doctor/consultation-form";
        }

        try {
            consultationService.saveConsultation(doctor, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Consultation completed successfully. Diagnosis recorded and prescriptions issued.");
            return "redirect:/doctor/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/doctor/consultations/start?appointmentId=" + dto.getAppointmentId();
        }
    }

    @PostMapping("/consultations/{id}/notes")
    public String updateConsultationNotes(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable("id") Long recordId,
                                          @RequestParam("clinicalNotes") String clinicalNotes,
                                          @RequestParam(value = "followUpInstructions", required = false) String followUpInstructions,
                                          RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        try {
            consultationService.updateConsultationNotes(doctor, recordId, clinicalNotes, followUpInstructions);
            redirectAttributes.addFlashAttribute("successMessage", "Consultation notes successfully updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/doctor/consultations";
    }

    // 5. Prescription Management
    @GetMapping("/prescriptions")
    public String prescriptionsList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        List<Prescription> prescriptions = consultationService.getDoctorPrescriptions(doctor);
        List<Patient> patients = appointmentRepository.findDistinctPatientsByDoctor(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("patients", patients);

        return "doctor/prescriptions";
    }

    @PostMapping("/prescriptions/new")
    public String issuePrescription(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam("patientId") Long patientId,
                                    @ModelAttribute PrescriptionItemDto rxDto,
                                    RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        try {
            consultationService.issueDirectPrescription(doctor, patientId, rxDto);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription issued successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/doctor/prescriptions";
    }

    // 6. Doctor Availability & Scheduling
    @GetMapping("/availability")
    public String availability(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        List<DoctorAvailability> availabilitySlots = availabilityService.getDoctorUpcomingAvailability(doctor);
        List<Appointment> todayAppointments = appointmentRepository.findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(doctor, LocalDate.now());

        model.addAttribute("doctor", doctor);
        model.addAttribute("slots", availabilitySlots);
        model.addAttribute("todayAppointments", todayAppointments);
        if (!model.containsAttribute("slotDto")) {
            DoctorAvailabilityDto slotDto = new DoctorAvailabilityDto();
            slotDto.setAvailableDate(LocalDate.now());
            model.addAttribute("slotDto", slotDto);
        }

        return "doctor/availability";
    }

    @PostMapping("/availability/add")
    public String addAvailabilitySlot(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @Valid @ModelAttribute("slotDto") DoctorAvailabilityDto slotDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid slot input. Please check required dates and times.");
            return "redirect:/doctor/availability";
        }

        try {
            availabilityService.addAvailabilitySlot(doctor, slotDto);
            redirectAttributes.addFlashAttribute("successMessage", "Consultation slot successfully added to your schedule.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/doctor/availability";
    }

    @PostMapping("/availability/{id}/delete")
    public String deleteAvailabilitySlot(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable("id") Long slotId,
                                         RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        try {
            availabilityService.deleteAvailabilitySlot(doctor, slotId);
            redirectAttributes.addFlashAttribute("successMessage", "Consultation slot removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/doctor/availability";
    }

    @PostMapping("/availability/toggle-today")
    public String toggleAvailabilityToday(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        Doctor doctor = getAuthenticatedDoctor(userDetails);
        doctorService.setDoctorAvailabilityToday(doctor, !doctor.isAvailableToday());
        redirectAttributes.addFlashAttribute("successMessage",
                "Your duty status has been set to: " + (!doctor.isAvailableToday() ? "Off Duty" : "On Duty"));
        return "redirect:/doctor/availability";
    }
}
