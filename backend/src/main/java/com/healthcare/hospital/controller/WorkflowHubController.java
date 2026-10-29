package com.healthcare.hospital.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Role-specific workflow launchpad. It deliberately links to existing feature
 * controllers rather than creating fake endpoints or duplicating persistence.
 */
@Controller
@RequestMapping("/workflows")
public class WorkflowHubController {
    private static final Map<String, List<Map<String, String>>> WORKFLOWS = Map.of(
        "patient", List.of(
            item("Book appointment", "Schedule a visit with an available doctor.", "/patient/appointments/book", "calendar-plus"),
            item("My appointments", "Review upcoming and previous visits.", "/patient/appointments", "calendar-days"),
            item("Medical history", "Review your recorded clinical history.", "/patient/medical-history", "file-heart"),
            item("Prescriptions", "Review prescriptions issued by your care team.", "/patient/prescriptions", "pill"),
            item("Lab reports", "Review completed and pending laboratory reports.", "/patient/lab-reports", "flask-conical"),
            item("Invoices & payments", "View invoices and payment status.", "/patient/invoices", "receipt"),
            item("Notifications", "Check updates from the hospital.", "/patient/notifications", "bell"),
            item("Profile", "Maintain your contact details.", "/patient/profile", "user-round")
        ),
        "receptionist", List.of(
            item("Patient directory", "Search and open registered patients.", "/receptionist/patients", "users"),
            item("Register patient", "Create a patient record at the front desk.", "/receptionist/patients/new", "user-plus"),
            item("Appointments", "Schedule, reschedule and cancel appointments.", "/receptionist/appointments", "calendar"),
            item("Schedule view", "Review appointment slots.", "/receptionist/appointments", "calendar-clock"),
            item("Waiting queue", "Track check-ins and queue progression.", "/receptionist/queue", "list-ordered"),
            item("Doctors", "Review doctor availability.", "/receptionist/doctors", "stethoscope")
        ),
        "doctor", List.of(
            item("Today's clinic", "Review your daily appointment list.", "/doctor/dashboard", "calendar-check"),
            item("Patients", "Open authorized patient records.", "/doctor/patients", "users"),
            item("Patient history", "Review medical history before consultation.", "/doctor/patients", "file-heart"),
            item("Prescriptions", "Review or manage prescriptions.", "/doctor/prescriptions", "pill"),
            item("Availability", "Manage available consultation slots.", "/doctor/availability", "clock")
        ),
        "pharmacy", List.of(
            item("Medicine catalogue", "Search and maintain medicines.", "/pharmacy/medicines", "pill"),
            item("Inventory batches", "Track batch quantities and expiry dates.", "/pharmacy/batches", "boxes"),
            item("Prescription queue", "Review prescriptions awaiting dispensing.", "/pharmacy/prescriptions", "clipboard-list"),
            item("Dispensations", "Review completed dispensing records.", "/pharmacy/dispensations", "package-check"),
            item("Suppliers & purchases", "Maintain suppliers and purchase orders.", "/pharmacy/suppliers", "truck"),
            item("Expiry alerts", "Find batches approaching expiry.", "/pharmacy/alerts/expiry", "triangle-alert"),
            item("Reports", "Review stock and dispensing reports.", "/pharmacy/reports", "chart-no-axes-combined")
        ),
        "laboratory", List.of(
            item("Lab dashboard", "Review pending and completed reports.", "/laboratory/dashboard", "flask-conical"),
            item("Sample tracking", "Track collection and sample status.", "/laboratory/samples", "test-tube"),
            item("Lab orders", "Review requested investigations.", "/laboratory/dashboard", "clipboard-list"),
            item("Result review", "Review results awaiting verification.", "/laboratory/dashboard", "file-check")
        ),
        "billing", List.of(
            item("Billing dashboard", "Review paid and outstanding invoices.", "/billing/dashboard", "wallet"),
            item("Payments", "Review recorded payments.", "/billing/payments", "credit-card"),
            item("Invoice adjustments", "Open authorized adjustment workflows.", "/billing/dashboard", "receipt"),
            item("Refund requests", "Review refund requests.", "/billing/dashboard", "undo-2"),
            item("Reconciliation", "Review payment reconciliation tasks.", "/billing/payments", "scale"),
            item("Financial periods", "Manage financial period records.", "/billing/dashboard", "calendar-range")
        ),
        "admin", List.of(
            item("Administration", "Hospital-wide account and system oversight.", "/admin/dashboard", "shield-check"),
            item("Audit trail", "Review recorded administrative events.", "/admin/audit", "scroll-text"),
            item("Settings", "Review configurable system settings.", "/admin/settings", "settings"),
            item("Hospital reports", "Review hospital-wide reporting.", "/reports", "chart-no-axes-combined"),
            item("Access review", "Review access and permission records.", "/admin/dashboard", "key-round"),
            item("Backup runs", "Review backup run records.", "/admin/dashboard", "database-backup"),
            item("Clinical quality register", "Investigate incidents, assign corrective actions and verify closure.", "/quality/reviews", "shield-check")
        ),
        "staff", List.of(
            item("Staff dashboard", "Review today's operational activity.", "/staff/dashboard", "layout-dashboard"),
            item("Ward & bed status", "Review ward and bed availability.", "/staff/wards", "bed"),
            item("Care tasks", "Track assigned patient-care tasks.", "/staff/tasks", "clipboard-check"),
            item("Shifts", "Review shift assignments and handover.", "/staff/shifts", "calendar-clock"),
            item("Attendance", "Review attendance records.", "/staff/attendance", "user-check"),
            item("Nursing observations", "Record and review authorized observations.", "/staff/tasks", "heart-pulse"),
            item("Incident reports", "Review incident report workflows.", "/staff/tasks", "shield-alert"),
            item("Clinical quality cases", "Manage investigations and corrective/preventive actions.", "/quality/reviews", "clipboard-check")
        )
    );

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public String patient(Model model) { return render("patient", "Patient Workspace", model); }

    @GetMapping("/receptionist")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public String receptionist(Model model) { return render("receptionist", "Receptionist Workspace", model); }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctor(Model model) { return render("doctor", "Doctor Workspace", model); }

    @GetMapping("/pharmacy")
    @PreAuthorize("hasAnyRole('PHARMACIST','ADMIN')")
    public String pharmacy(Model model) { return render("pharmacy", "Pharmacy Workspace", model); }

    @GetMapping("/laboratory")
    @PreAuthorize("hasAnyRole('LAB_TECHNICIAN','ADMIN')")
    public String laboratory(Model model) { return render("laboratory", "Laboratory Workspace", model); }

    @GetMapping("/billing")
    @PreAuthorize("hasAnyRole('BILLING','ADMIN')")
    public String billing(Model model) { return render("billing", "Billing & Finance Workspace", model); }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(Model model) { return render("admin", "Administration Workspace", model); }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('STAFF','NURSE','ADMIN')")
    public String staff(Model model) { return render("staff", "Staff & Nursing Workspace", model); }

    private String render(String role, String title, Model model) {
        model.addAttribute("workspaceTitle", title);
        model.addAttribute("workspaceRole", role);
        model.addAttribute("workflows", WORKFLOWS.get(role));
        model.addAttribute("activeNav", "workflow-hub");
        return "workflows/hub";
    }

    private static Map<String, String> item(String title, String description, String url, String icon) {
        return Map.of("title", title, "description", description, "url", url, "icon", icon);
    }
}
