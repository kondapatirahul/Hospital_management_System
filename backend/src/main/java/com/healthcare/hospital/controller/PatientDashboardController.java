package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.AppointmentBookingDto;
import com.healthcare.hospital.dto.AppointmentRescheduleDto;
import com.healthcare.hospital.dto.PatientProfileDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientDashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final MedicalService medicalService;
    private final BillingService billingService;
    private final NotificationService notificationService;
    private final DoctorService doctorService;

    public PatientDashboardController(PatientService patientService,
                                      AppointmentService appointmentService,
                                      MedicalService medicalService,
                                      BillingService billingService,
                                      NotificationService notificationService,
                                      DoctorService doctorService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.medicalService = medicalService;
        this.billingService = billingService;
        this.notificationService = notificationService;
        this.doctorService = doctorService;
    }

    private Patient getAuthenticatedPatient(CustomUserDetails userDetails) {
        return patientService.getPatientByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("No patient record linked to logged in user"));
    }

    // 1. Dashboard Overview
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        List<Appointment> upcoming = appointmentService.getPatientUpcomingAppointments(patient);
        List<Prescription> prescriptions = medicalService.getPrescriptions(patient);
        List<LabReport> labReports = medicalService.getLabReports(patient);
        List<Invoice> invoices = billingService.getPatientInvoices(patient);
        List<Notification> notifications = notificationService.getNotificationsForUser(userDetails.getUser());

        long pendingInvoices = invoices.stream()
                .filter(i -> i.getStatus().name().equals("PENDING"))
                .count();

        long pendingLabs = labReports.stream()
                .filter(l -> l.getStatus().name().equals("PENDING"))
                .count();

        model.addAttribute("patient", patient);
        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("upcomingCount", upcoming.size());
        model.addAttribute("activePrescriptionsCount", prescriptions.stream().filter(p -> "Active".equalsIgnoreCase(p.getStatus())).count());
        model.addAttribute("pendingInvoicesCount", pendingInvoices);
        model.addAttribute("pendingLabsCount", pendingLabs);
        model.addAttribute("recentNotifications", notifications.stream().limit(5).toList());
        model.addAttribute("recentPrescriptions", prescriptions.stream().limit(3).toList());
        model.addAttribute("recentLabs", labReports.stream().limit(3).toList());

        return "patient/dashboard";
    }

    // 2. Profile Management
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);

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

        model.addAttribute("patient", patient);
        model.addAttribute("profileDto", dto);
        return "patient/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute("profileDto") PatientProfileDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Patient patient = getAuthenticatedPatient(userDetails);

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            return "patient/profile";
        }

        patientService.updatePatientProfile(patient.getId(), dto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully!");
        return "redirect:/patient/profile";
    }

    // 3. Appointments (Upcoming & Past)
    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("upcomingAppointments", appointmentService.getPatientUpcomingAppointments(patient));
        model.addAttribute("pastAppointments", appointmentService.getPatientPastAppointments(patient));
        model.addAttribute("rescheduleDto", new AppointmentRescheduleDto());
        return "patient/appointments";
    }

    // Book Appointment
    @GetMapping("/appointments/book")
    public String bookAppointmentPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        if (!model.containsAttribute("bookingDto")) {
            AppointmentBookingDto dto = new AppointmentBookingDto();
            dto.setPatientId(patient.getId());
            model.addAttribute("bookingDto", dto);
        }
        model.addAttribute("patient", patient);
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "patient/book-appointment";
    }

    @PostMapping("/appointments/book")
    public String handleBookAppointment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @Valid @ModelAttribute("bookingDto") AppointmentBookingDto dto,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Patient patient = getAuthenticatedPatient(userDetails);

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            return "patient/book-appointment";
        }

        appointmentService.bookAppointment(dto, patient);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully! Your visit details are confirmed.");
        return "redirect:/patient/appointments";
    }

    // Reschedule Appointment
    @PostMapping("/appointments/{id}/reschedule")
    public String handleReschedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @PathVariable("id") Long appointmentId,
                                   @Valid @ModelAttribute("rescheduleDto") AppointmentRescheduleDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        Patient patient = getAuthenticatedPatient(userDetails);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        // Security check: ensure appointment belongs to this patient
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized action.");
            return "redirect:/patient/appointments";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid reschedule parameters. Please check date and time.");
            return "redirect:/patient/appointments";
        }

        appointmentService.rescheduleAppointment(appointmentId, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment rescheduled successfully!");
        return "redirect:/patient/appointments";
    }

    // Cancel Appointment
    @PostMapping("/appointments/{id}/cancel")
    public String handleCancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable("id") Long appointmentId,
                               @RequestParam(value = "cancellationReason", required = false) String reason,
                               RedirectAttributes redirectAttributes) {
        Patient patient = getAuthenticatedPatient(userDetails);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized action.");
            return "redirect:/patient/appointments";
        }

        appointmentService.cancelAppointment(appointmentId, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment has been cancelled.");
        return "redirect:/patient/appointments";
    }

    // 4. Medical History & Previous Visits
    @GetMapping("/medical-history")
    public String medicalHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("records", medicalService.getMedicalRecords(patient));
        return "patient/medical-history";
    }

    // 5. Prescriptions & Medicine Instructions
    @GetMapping("/prescriptions")
    public String prescriptions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("prescriptions", medicalService.getPrescriptions(patient));
        return "patient/prescriptions";
    }

    // 6. Laboratory Reports & Status
    @GetMapping("/lab-reports")
    public String labReports(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("reports", medicalService.getLabReports(patient));
        return "patient/lab-reports";
    }

    // 7. Invoices & Billing
    @GetMapping("/invoices")
    public String invoices(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getAuthenticatedPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("invoices", billingService.getPatientInvoices(patient));
        return "patient/invoices";
    }

    @PostMapping("/invoices/{id}/pay")
    public String payInvoice(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long invoiceId,
                             @RequestParam("paymentMethod") String paymentMethod,
                             RedirectAttributes redirectAttributes) {
        Patient patient = getAuthenticatedPatient(userDetails);
        Invoice invoice = billingService.getInvoiceById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        if (!invoice.getPatient().getId().equals(patient.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized action.");
            return "redirect:/patient/invoices";
        }

        billingService.markInvoiceAsPaid(invoiceId, paymentMethod);
        redirectAttributes.addFlashAttribute("successMessage", "Payment processed successfully for " + invoice.getInvoiceNumber() + "!");
        return "redirect:/patient/invoices";
    }

    // 8. Notifications & Reminders
    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("notifications", notificationService.getNotificationsForUser(userDetails.getUser()));
        return "patient/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable("id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return "redirect:/patient/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUser());
        return "redirect:/patient/notifications";
    }
}
