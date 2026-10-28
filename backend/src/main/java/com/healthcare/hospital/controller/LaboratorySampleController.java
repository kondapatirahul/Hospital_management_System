package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.LabReportRepository;
import com.healthcare.hospital.service.LaboratorySampleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/laboratory/samples")
@PreAuthorize("hasAnyRole('LAB_TECHNICIAN', 'ADMIN')")
public class LaboratorySampleController {
    private final LaboratorySampleService sampleService;
    private final LabReportRepository reportRepository;

    public LaboratorySampleController(LaboratorySampleService sampleService, LabReportRepository reportRepository) {
        this.sampleService = sampleService;
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("samples", sampleService.all());
        model.addAttribute("reports", reportRepository.findAll());
        return "laboratory/samples";
    }

    @PostMapping
    public String create(@RequestParam Long reportId, @RequestParam String sampleCode,
                         @RequestParam String sampleType, RedirectAttributes flash) {
        try {
            sampleService.create(reportId, sampleCode, sampleType);
            flash.addFlashAttribute("successMessage", "Sample registered.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/laboratory/samples";
    }

    @PostMapping("/{id}/collect")
    public String collect(@PathVariable Long id, @RequestParam String collectedBy,
                          @RequestParam(required = false) String notes, RedirectAttributes flash) {
        try {
            sampleService.collect(id, collectedBy, notes);
            flash.addFlashAttribute("successMessage", "Sample collection recorded.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/laboratory/samples";
    }

    @PostMapping("/{id}/receive")
    public String receive(@PathVariable Long id, RedirectAttributes flash) {
        try {
            sampleService.receive(id);
            flash.addFlashAttribute("successMessage", "Sample received by the laboratory.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/laboratory/samples";
    }

    @PostMapping("/{id}/process")
    public String process(@PathVariable Long id, RedirectAttributes flash) {
        try {
            sampleService.startProcessing(id);
            flash.addFlashAttribute("successMessage", "Sample moved to processing.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/laboratory/samples";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam String reason, RedirectAttributes flash) {
        try {
            sampleService.reject(id, reason);
            flash.addFlashAttribute("successMessage", "Sample rejection recorded.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/laboratory/samples";
    }
}
