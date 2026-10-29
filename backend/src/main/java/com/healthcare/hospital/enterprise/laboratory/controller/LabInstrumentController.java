package com.healthcare.hospital.enterprise.laboratory.controller;

import com.healthcare.hospital.enterprise.laboratory.entity.LabInstrument;
import com.healthcare.hospital.enterprise.laboratory.service.LabInstrumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for laboratory instrument maintenance.
 * The shared security configuration continues to control access to /laboratory/enterprise/**.
 */
@Controller
@RequestMapping("/laboratory/enterprise/labInstrument")
public class LabInstrumentController {
    private final LabInstrumentService service;

    public LabInstrumentController(LabInstrumentService service) {
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
        model.addAttribute("recordType", "LabInstrument");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/laboratory/labInstrument";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new LabInstrument());
        model.addAttribute("formAction", "/laboratory/enterprise/labInstrument");
        return "enterprise/laboratory/labInstrument-form";
    }

    @PostMapping
    public String create(@ModelAttribute LabInstrument record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/laboratory/enterprise/labInstrument";
        }
        return "redirect:/laboratory/enterprise/labInstrument";
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
        return "redirect:/laboratory/enterprise/labInstrument";
    }
}
