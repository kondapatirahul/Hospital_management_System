package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.LabReport;
import com.healthcare.hospital.enums.LabReportStatus;
import com.healthcare.hospital.repository.LabReportRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/laboratory")
@PreAuthorize("hasAnyRole('LAB_TECHNICIAN', 'ADMIN')")
@Validated
public class LaboratoryDashboardController {
    private final LabReportRepository labReportRepository;
    public LaboratoryDashboardController(LabReportRepository labReportRepository) { this.labReportRepository = labReportRepository; }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        var reports = labReportRepository.findAll();
        model.addAttribute("reports", reports);
        model.addAttribute("pendingCount", reports.stream().filter(r -> r.getStatus() == LabReportStatus.PENDING).count());
        model.addAttribute("completedCount", reports.stream().filter(r -> r.getStatus() == LabReportStatus.COMPLETED).count());
        model.addAttribute("reviewedCount", reports.stream().filter(r -> r.getStatus() == LabReportStatus.REVIEWED).count());
        model.addAttribute("statuses", LabReportStatus.values());
        return "laboratory/dashboard";
    }

    @PostMapping("/reports/{id}/complete")
    public String completeReport(@PathVariable Long id, @RequestParam @NotBlank String summaryResult,
                                 @RequestParam(required = false) String referenceRange,
                                 @RequestParam(required = false) String doctorNotes,
                                 RedirectAttributes redirectAttributes) {
        LabReport report = labReportRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Lab report not found: " + id));
        report.setSummaryResult(summaryResult.trim());
        report.setReferenceRange(referenceRange == null ? null : referenceRange.trim());
        report.setDoctorNotes(doctorNotes == null ? null : doctorNotes.trim());
        report.setStatus(LabReportStatus.COMPLETED);
        report.setCompletedDate(LocalDate.now());
        labReportRepository.save(report);
        redirectAttributes.addFlashAttribute("successMessage", "Lab report marked completed.");
        return "redirect:/laboratory/dashboard";
    }
}
