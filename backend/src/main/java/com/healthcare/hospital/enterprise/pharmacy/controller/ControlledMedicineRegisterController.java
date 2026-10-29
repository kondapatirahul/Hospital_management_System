package com.healthcare.hospital.enterprise.pharmacy.controller;

import com.healthcare.hospital.enterprise.pharmacy.entity.ControlledMedicineRegister;
import com.healthcare.hospital.enterprise.pharmacy.service.ControlledMedicineRegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for controlled medicine register.
 * The shared security configuration continues to control access to /pharmacy/enterprise/**.
 */
@Controller
@RequestMapping("/pharmacy/enterprise/controlledMedicineRegister")
public class ControlledMedicineRegisterController {
    private final ControlledMedicineRegisterService service;

    public ControlledMedicineRegisterController(ControlledMedicineRegisterService service) {
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
        model.addAttribute("recordType", "ControlledMedicineRegister");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/pharmacy/controlledMedicineRegister";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new ControlledMedicineRegister());
        model.addAttribute("formAction", "/pharmacy/enterprise/controlledMedicineRegister");
        return "enterprise/pharmacy/controlledMedicineRegister-form";
    }

    @PostMapping
    public String create(@ModelAttribute ControlledMedicineRegister record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/pharmacy/enterprise/controlledMedicineRegister";
        }
        return "redirect:/pharmacy/enterprise/controlledMedicineRegister";
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
        return "redirect:/pharmacy/enterprise/controlledMedicineRegister";
    }
}
