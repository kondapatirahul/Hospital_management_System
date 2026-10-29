package com.healthcare.hospital.enterprise.staff.controller;

import com.healthcare.hospital.enterprise.staff.entity.PatientTransfer;
import com.healthcare.hospital.enterprise.staff.service.PatientTransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for patient transfer workflow.
 * The shared security configuration continues to control access to /staff/enterprise/**.
 */
@Controller
@RequestMapping("/staff/enterprise/patientTransfer")
public class PatientTransferController {
    private final PatientTransferService service;

    public PatientTransferController(PatientTransferService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required=false) String status,
                        @RequestParam(required=false) String assignee, Model model) {
        model.addAttribute("records", service.list(status, assignee));
        model.addAttribute("overdueRecords", service.overdue());
        model.addAttribute("totalCount", service.countAll());
        model.addAttribute("openCount", service.countOpen());
        model.addAttribute("completedCount", service.countCompleted());
        model.addAttribute("overdueCount", service.countOverdue());
        model.addAttribute("recordType", "PatientTransfer");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/staff/patientTransfer";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new PatientTransfer());
        model.addAttribute("formAction", "/staff/enterprise/patientTransfer");
        return "enterprise/staff/patientTransfer-form";
    }

    @PostMapping
    public String create(@ModelAttribute PatientTransfer record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/enterprise/patientTransfer";
        }
        return "redirect:/staff/enterprise/patientTransfer";
    }

    @PostMapping("/{id}/start")
    public String start(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.start(id, principal == null ? "system" : principal.getName()), flash, "Record started.");
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.complete(id, principal == null ? "system" : principal.getName()), flash, "Record completed.");
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam String reason, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.cancel(id, principal == null ? "system" : principal.getName(), reason), flash, "Record cancelled.");
    }

    private String transition(Runnable action, RedirectAttributes flash, String message) {
        try {
            action.run();
            flash.addFlashAttribute("successMessage", message);
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/enterprise/patientTransfer";
    }
}
