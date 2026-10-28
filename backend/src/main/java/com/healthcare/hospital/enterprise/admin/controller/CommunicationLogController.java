package com.healthcare.hospital.enterprise.admin.controller;

import com.healthcare.hospital.enterprise.admin.entity.CommunicationLog;
import com.healthcare.hospital.enterprise.admin.service.CommunicationLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for patient communication history.
 * The shared security configuration continues to control access to /admin/enterprise/**.
 */
@Controller
@RequestMapping("/admin/enterprise/communicationLog")
public class CommunicationLogController {
    private final CommunicationLogService service;

    public CommunicationLogController(CommunicationLogService service) {
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
        model.addAttribute("recordType", "CommunicationLog");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/admin/communicationLog";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new CommunicationLog());
        model.addAttribute("formAction", "/admin/enterprise/communicationLog");
        return "enterprise/admin/communicationLog-form";
    }

    @PostMapping
    public String create(@ModelAttribute CommunicationLog record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/enterprise/communicationLog";
        }
        return "redirect:/admin/enterprise/communicationLog";
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
        return "redirect:/admin/enterprise/communicationLog";
    }
}
