package com.healthcare.hospital.enterprise.admin.controller;

import com.healthcare.hospital.enterprise.admin.entity.StaffCredential;
import com.healthcare.hospital.enterprise.admin.service.StaffCredentialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for staff credential and expiry tracking.
 * The shared security configuration continues to control access to /admin/enterprise/**.
 */
@Controller
@RequestMapping("/admin/enterprise/staffCredential")
public class StaffCredentialController {
    private final StaffCredentialService service;

    public StaffCredentialController(StaffCredentialService service) {
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
        model.addAttribute("recordType", "StaffCredential");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/admin/staffCredential";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new StaffCredential());
        model.addAttribute("formAction", "/admin/enterprise/staffCredential");
        return "enterprise/admin/staffCredential-form";
    }

    @PostMapping
    public String create(@ModelAttribute StaffCredential record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/enterprise/staffCredential";
        }
        return "redirect:/admin/enterprise/staffCredential";
    }

    @PostMapping("/<built-in function id>/start")
    public String start(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.start(id, principal == null ? "system" : principal.getName()), flash, "Record started.");
    }

    @PostMapping("/<built-in function id>/complete")
    public String complete(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.complete(id, principal == null ? "system" : principal.getName()), flash, "Record completed.");
    }

    @PostMapping("/<built-in function id>/cancel")
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
        return "redirect:/admin/enterprise/staffCredential";
    }
}
