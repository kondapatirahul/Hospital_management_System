package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.AppointmentBookingDto;
import com.healthcare.hospital.dto.AppointmentFilterDto;
import com.healthcare.hospital.dto.AppointmentRescheduleDto;
import com.healthcare.hospital.dto.PatientProfileDto;
import com.healthcare.hospital.dto.PatientRegistrationDto;
import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.QueueEntry;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.enums.QueueStatus;
import com.healthcare.hospital.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistDashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final QueueService queueService;
    private final DoctorService doctorService;
    private final MedicalService medicalService;
    private final BillingService billingService;

    public ReceptionistDashboardController(PatientService patientService,
                                          AppointmentService appointmentService,
                                          QueueService queueService,
                                          DoctorService doctorService,
                                          MedicalService medicalService,
                                          BillingService billingService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.queueService = queueService;
        this.doctorService = doctorService;
        this.medicalService = medicalService;
        this.billingService = billingService;
    }

    // 1. Receptionist Dashboard Overview
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalToday = appointmentService.countAppointmentsToday();
        long checkedInToday = appointmentService.countAppointmentsByStatusToday(AppointmentStatus.CHECKED_IN);
        long inConsultationToday = appointmentService.countAppointmentsByStatusToday(AppointmentStatus.IN_CONSULTATION);
        long completedToday = appointmentService.countAppointmentsByStatusToday(AppointmentStatus.COMPLETED);
        long waitingInQueue = queueService.getWaitingCountToday();

        List<QueueEntry> todayQueue = queueService.getTodayQueue();
        List<Appointment> todayAppointments = appointmentService.getTodayAppointments();
        List<Doctor> doctors = doctorService.getAllDoctors();

        model.addAttribute("totalToday", totalToday);
        model.addAttribute("checkedInToday", checkedInToday);
        model.addAttribute("inConsultationToday", inConsultationToday);
        model.addAttribute("completedToday", completedToday);
        model.addAttribute("waitingInQueue", waitingInQueue);
        model.addAttribute("todayQueue", todayQueue);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("doctors", doctors);
        model.addAttribute("activeDoctorsCount", doctors.stream().filter(Doctor::isAvailableToday).count());
        model.addAttribute("totalPatientsCount", patientService.getAllPatients().size());

        return "receptionist/dashboard";
    }

    // 2. Patient Management & Search
    @GetMapping("/patients")
    public String patientsList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Patient> patients = patientService.searchPatients(keyword);
        model.addAttribute("patients", patients);
        model.addAttribute("keyword", keyword);
        return "receptionist/patients";
    }

    // Register New Patient with Auto-generated ID
    @GetMapping("/patients/new")
    public String newPatientForm(Model model) {
        if (!model.containsAttribute("patientDto")) {
            PatientRegistrationDto dto = new PatientRegistrationDto();
            dto.setCreateAccount(false); // Default to clinic record, optional portal access
            model.addAttribute("patientDto", dto);
        }
        model.addAttribute("previewPatientId", patientService.generateNextPatientId());
        return "receptionist/patient-register";
    }

    @PostMapping("/patients/new")
    public String handleRegisterPatient(@Valid @ModelAttribute("patientDto") PatientRegistrationDto dto,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("previewPatientId", patientService.generateNextPatientId());
            return "receptionist/patient-register";
        }

        try {
            Patient saved = patientService.registerPatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient registered successfully! Generated Patient ID: " + saved.getPatientId());
            return "redirect:/receptionist/patients/" + saved.getId();
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("previewPatientId", patientService.generateNextPatientId());
            return "receptionist/patient-register";
        }
    }

    // View Patient Details
    @GetMapping("/patients/{id}")
    public String viewPatient(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + id));

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentService.getPatientAllAppointments(patient));
        model.addAttribute("medicalRecords", medicalService.getMedicalRecords(patient));
        model.addAttribute("prescriptions", medicalService.getPrescriptions(patient));
        model.addAttribute("invoices", billingService.getPatientInvoices(patient));
        return "receptionist/patient-details";
    }

    // Edit Patient Details
    @GetMapping("/patients/{id}/edit")
    public String editPatientForm(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + id));

        if (!model.containsAttribute("profileDto")) {
            PatientProfileDto dto = new PatientProfileDto();
            dto.setFullName(patient.getFullName());
            dto.setEmail(patient.getEmail());
            dto.setPhone(patient.getPhone());
            dto.setAddress(patient.getAddress());
            dto.setEmergencyContactName(patient.getEmergencyContactName());
            dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
            dto.setBloodGroup(patient.getBloodGroup());
            dto.setAllergies(patient.getAllergies());
            dto.setChronicConditions(patient.getChronicConditions());
            model.addAttribute("profileDto", dto);
        }

        model.addAttribute("patient", patient);
        return "receptionist/patient-edit";
    }

    @PostMapping("/patients/{id}/edit")
    public String handleUpdatePatient(@PathVariable("id") Long id,
                                      @Valid @ModelAttribute("profileDto") PatientProfileDto dto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Patient patient = patientService.getPatientById(id).orElseThrow();
            model.addAttribute("patient", patient);
            return "receptionist/patient-edit";
        }

        patientService.updatePatientProfile(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Patient details updated successfully!");
        return "redirect:/receptionist/patients/" + id;
    }

    // 3. Appointments List, Search, Sort & Filter
    @GetMapping("/appointments")
    public String appointmentsList(@ModelAttribute("filter") AppointmentFilterDto filter, Model model) {
        List<Appointment> appointments = appointmentService.filterAppointments(filter);
        model.addAttribute("appointments", appointments);
        model.addAttribute("filter", filter);
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("rescheduleDto", new AppointmentRescheduleDto());
        return "receptionist/appointments";
    }

    // Schedule New Appointment for Patient
    @GetMapping("/appointments/new")
    public String scheduleAppointmentForm(@RequestParam(value = "patientId", required = false) Long patientId,
                                          Model model) {
        if (!model.containsAttribute("bookingDto")) {
            AppointmentBookingDto dto = new AppointmentBookingDto();
            if (patientId != null) {
                dto.setPatientId(patientId);
            }
            model.addAttribute("bookingDto", dto);
        }

        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "receptionist/appointment-schedule";
    }

    @PostMapping("/appointments/new")
    public String handleScheduleAppointment(@Valid @ModelAttribute("bookingDto") AppointmentBookingDto dto,
                                            BindingResult bindingResult,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        if (dto.getPatientId() == null) {
            bindingResult.rejectValue("patientId", "error.patientId", "Please select a patient");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            return "receptionist/appointment-schedule";
        }

        Patient patient = patientService.getPatientById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));

        appointmentService.bookAppointment(dto, patient);
        redirectAttributes.addFlashAttribute("successMessage",
                "Appointment successfully scheduled for " + patient.getFullName() + " on " + dto.getAppointmentDate());
        return "redirect:/receptionist/appointments";
    }

    // Reschedule Appointment
    @PostMapping("/appointments/{id}/reschedule")
    public String handleReschedule(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("rescheduleDto") AppointmentRescheduleDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid reschedule date or time.");
            return "redirect:/receptionist/appointments";
        }

        appointmentService.rescheduleAppointment(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment rescheduled successfully!");
        return "redirect:/receptionist/appointments";
    }

    // Cancel Appointment
    @PostMapping("/appointments/{id}/cancel")
    public String handleCancel(@PathVariable("id") Long id,
                               @RequestParam(value = "cancellationReason", required = false) String reason,
                               RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully.");
        return "redirect:/receptionist/appointments";
    }

    // 4. Check-in & Check-out Flow
    @PostMapping("/appointments/{id}/check-in")
    public String handleCheckIn(@PathVariable("id") Long appointmentId, RedirectAttributes redirectAttributes) {
        QueueEntry entry = queueService.checkInPatient(appointmentId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Patient checked in successfully! Token Issued: " + entry.getQueueNumber());
        return "redirect:/receptionist/queue";
    }

    @PostMapping("/appointments/{id}/check-out")
    public String handleCheckOut(@PathVariable("id") Long appointmentId, RedirectAttributes redirectAttributes) {
        queueService.checkOutDirectly(appointmentId);
        redirectAttributes.addFlashAttribute("successMessage", "Patient checked out and visit marked completed.");
        return "redirect:/receptionist/appointments";
    }

    // 5. Live Waiting Queue Management
    @GetMapping("/queue")
    public String waitingQueue(Model model) {
        List<QueueEntry> queue = queueService.getTodayQueue();
        model.addAttribute("queue", queue);
        model.addAttribute("waitingCount", queueService.getWaitingCountToday());
        model.addAttribute("inConsultationCount", queueService.getInConsultationCountToday());
        model.addAttribute("completedCount", queueService.getCompletedCountToday());
        return "receptionist/queue";
    }

    @PostMapping("/queue/{id}/call")
    public String callNextPatientInQueue(@PathVariable("id") Long queueId, RedirectAttributes redirectAttributes) {
        QueueEntry entry = queueService.callNextPatient(queueId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Called token " + entry.getQueueNumber() + " (" + entry.getPatient().getFullName() + ") into " + entry.getDoctor().getRoomNumber());
        return "redirect:/receptionist/queue";
    }

    @PostMapping("/queue/{id}/complete")
    public String completePatientInQueue(@PathVariable("id") Long queueId, RedirectAttributes redirectAttributes) {
        QueueEntry entry = queueService.completeConsultation(queueId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Consultation completed for token " + entry.getQueueNumber());
        return "redirect:/receptionist/queue";
    }

    @PostMapping("/queue/{id}/skip")
    public String skipPatientInQueue(@PathVariable("id") Long queueId, RedirectAttributes redirectAttributes) {
        QueueEntry entry = queueService.skipPatient(queueId);
        redirectAttributes.addFlashAttribute("infoMessage", "Token " + entry.getQueueNumber() + " marked as skipped.");
        return "redirect:/receptionist/queue";
    }

    // 6. Doctor Availability & Daily Schedule
    @GetMapping("/doctors")
    public String doctorSchedules(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("todayAppointments", appointmentService.getTodayAppointments());
        return "receptionist/doctors";
    }

    @PostMapping("/doctors/{id}/toggle-availability")
    public String toggleDoctorAvailability(@PathVariable("id") Long doctorId, RedirectAttributes redirectAttributes) {
        doctorService.toggleDoctorAvailability(doctorId);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor availability status updated.");
        return "redirect:/receptionist/doctors";
    }
}
