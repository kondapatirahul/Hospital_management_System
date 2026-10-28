package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.PatientRepository;
import com.healthcare.hospital.service.CareTaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/staff/tasks")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
public class CareTaskController {
    private final CareTaskService tasks;
    private final PatientRepository patients;

    public CareTaskController(CareTaskService tasks, PatientRepository patients) {
        this.tasks = tasks; this.patients = patients;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tasks", tasks.all());
        model.addAttribute("patients", patients.findAll());
        return "staff/tasks";
    }

    @PostMapping
    public String create(@RequestParam String title, @RequestParam String taskType,
                         @RequestParam(defaultValue = "NORMAL") String priority,
                         @RequestParam(required = false) Long patientId,
                         @RequestParam(required = false) String assignedTo,
                         @RequestParam(required = false) String ward,
                         @RequestParam(required = false) String dueAt,
                         @RequestParam(required = false) String instructions,
                         RedirectAttributes flash) {
        try {
            LocalDateTime due = dueAt == null || dueAt.isBlank() ? null : LocalDateTime.parse(dueAt);
            tasks.create(title, taskType, priority, patientId, assignedTo, ward, due, instructions);
            flash.addFlashAttribute("successMessage", "Care task created.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/tasks";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, @RequestParam String status,
                         Principal principal, RedirectAttributes flash) {
        try {
            tasks.changeStatus(id, status, principal == null ? "staff-user" : principal.getName());
            flash.addFlashAttribute("successMessage", "Care task status updated.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/tasks";
    }
}
