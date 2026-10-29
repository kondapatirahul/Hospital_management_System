package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.SafetyIncidentForm;
import com.healthcare.hospital.entity.SafetyIncident;
import com.healthcare.hospital.service.SafetyIncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Role-protected UI endpoints for reporting and reviewing patient-safety events. */
@Controller
@RequestMapping("/safety/incidents")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','RECEPTIONIST','PHARMACY','LABORATORY')")
public class SafetyIncidentController {
    private final SafetyIncidentService incidents;
    public SafetyIncidentController(SafetyIncidentService incidents) { this.incidents = incidents; }

    @GetMapping
    public String index(@RequestParam(required=false) String status,
                        @RequestParam(required=false) String severity,
                        @RequestParam(required=false) String department,
                        @RequestParam(required=false) String q, Model model) {
        model.addAttribute("incidents", incidents.search(status, severity, department, q));
        model.addAttribute("metrics", incidents.metrics());
        model.addAttribute("filterStatus", status == null ? "" : status);
        model.addAttribute("filterSeverity", severity == null ? "" : severity);
        model.addAttribute("filterDepartment", department == null ? "" : department);
        model.addAttribute("filterQuery", q == null ? "" : q);
        model.addAttribute("departments", List.of("Emergency", "Outpatient", "Inpatient", "Pharmacy", "Laboratory", "Radiology", "Billing", "Facilities", "Other"));
        return "safety/incidents";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", new SafetyIncidentForm());
        model.addAttribute("categories", List.of("PATIENT_FALL", "MEDICATION", "INFECTION_CONTROL", "EQUIPMENT", "IDENTIFICATION", "DOCUMENTATION", "NEAR_MISS", "STAFF_SAFETY", "ENVIRONMENT", "OTHER"));
        model.addAttribute("departments", List.of("Emergency", "Outpatient", "Inpatient", "Pharmacy", "Laboratory", "Radiology", "Billing", "Facilities", "Other"));
        return "safety/incident-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") SafetyIncidentForm form,
                         BindingResult binding, Principal principal, RedirectAttributes flash, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("categories", List.of("PATIENT_FALL", "MEDICATION", "INFECTION_CONTROL", "EQUIPMENT", "IDENTIFICATION", "DOCUMENTATION", "NEAR_MISS", "STAFF_SAFETY", "ENVIRONMENT", "OTHER"));
            model.addAttribute("departments", List.of("Emergency", "Outpatient", "Inpatient", "Pharmacy", "Laboratory", "Radiology", "Billing", "Facilities", "Other"));
            return "safety/incident-form";
        }
        try {
            SafetyIncident saved = incidents.report(form, actor(principal));
            flash.addFlashAttribute("successMessage", "Incident " + saved.getReferenceCode() + " was reported securely.");
            return "redirect:/safety/incidents/" + saved.getId();
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/safety/incidents/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model, RedirectAttributes flash) {
        var incident = incidents.find(id);
        if (incident.isEmpty()) {
            flash.addFlashAttribute("errorMessage", "That incident could not be found.");
            return "redirect:/safety/incidents";
        }
        model.addAttribute("incident", incident.get());
        return "safety/incident-detail";
    }

    @PostMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public String acknowledge(@PathVariable long id, Principal principal, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.acknowledge(id, actor(principal)), "Incident acknowledged.");
    }

    @PostMapping("/{id}/investigate")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public String investigate(@PathVariable long id, @RequestParam(required=false) String assignedTo,
                              Principal principal, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.beginInvestigation(id, actor(principal), assignedTo), "Investigation started.");
    }

    @PostMapping("/{id}/investigation")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public String saveInvestigation(@PathVariable long id, @RequestParam(required=false) String rootCause,
                                    @RequestParam(required=false) String correctiveAction,
                                    @RequestParam(required=false) String preventionPlan,
                                    @RequestParam(required=false) String reviewNotes, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.saveInvestigation(id, rootCause, correctiveAction, preventionPlan, reviewNotes), "Investigation notes saved.");
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public String resolve(@PathVariable long id, @RequestParam String summary, Principal principal, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.resolve(id, actor(principal), summary), "Incident marked resolved.");
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public String close(@PathVariable long id, @RequestParam(required=false) String note, Principal principal, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.close(id, actor(principal), note), "Incident closed.");
    }

    @PostMapping("/{id}/reassign")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public String reassign(@PathVariable long id, @RequestParam String assignedTo, RedirectAttributes flash) {
        return action(id, flash, () -> incidents.reassign(id, assignedTo), "Incident assignment updated.");
    }

    @GetMapping(value="/export.csv", produces="text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> export(@RequestParam(required=false) String status,
                                         @RequestParam(required=false) String severity,
                                         @RequestParam(required=false) String department,
                                         @RequestParam(required=false) String q) {
        StringBuilder csv = new StringBuilder("Reference,Title,Category,Severity,Status,Department,Reporter,Assigned To,Occurred At,Reported At\r\n");
        for (SafetyIncident item : incidents.exportRows(status, severity, department, q)) {
            csv.append(cell(item.getReferenceCode())).append(',').append(cell(item.getTitle())).append(',')
                .append(cell(item.getCategory())).append(',').append(cell(item.getSeverity())).append(',')
                .append(cell(item.getStatus())).append(',').append(cell(item.getDepartment())).append(',')
                .append(cell(item.getReportedBy())).append(',').append(cell(item.getAssignedTo())).append(',')
                .append(cell(String.valueOf(item.getOccurredAt()))).append(',').append(cell(String.valueOf(item.getReportedAt()))).append("\r\n");
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=careflow-safety-incidents.csv")
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String action(long id, RedirectAttributes flash, Runnable work, String success) {
        try { work.run(); flash.addFlashAttribute("successMessage", success); }
        catch (RuntimeException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/safety/incidents/" + id;
    }
    private static String actor(Principal principal) { return principal == null ? "authenticated-user" : principal.getName(); }
    
private static String cell(String raw) {
    if (raw == null) {
        return "\"\"";
    }

    String safe = raw.replace("\"", "\"\"");

    // Prevent spreadsheet formula injection in CSV exports.
    if (safe.startsWith("=") || safe.startsWith("+")
            || safe.startsWith("-") || safe.startsWith("@")) {
        safe = "'" + safe;
    }

    return "\"" + safe + "\"";
}

}
