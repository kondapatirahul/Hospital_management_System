package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.LabReport;
import com.healthcare.hospital.enums.LabReportStatus;
import com.healthcare.hospital.repository.LabReportRepository;
import com.healthcare.hospital.repository.LabSampleRepository;
import com.healthcare.hospital.enterprise.laboratory.commandcenter.LaboratoryCommandCenterService;
import com.healthcare.hospital.enterprise.laboratory.commandcenter.LabDashboardSignalSnapshot;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private final LabSampleRepository labSampleRepository;
    private final LaboratoryCommandCenterService commandCenter;
    public LaboratoryDashboardController(LabReportRepository labReportRepository,
                                         LabSampleRepository labSampleRepository,
                                         LaboratoryCommandCenterService commandCenter) {
        this.labReportRepository = labReportRepository;
        this.labSampleRepository = labSampleRepository;
        this.commandCenter = commandCenter;
    }

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


    @GetMapping("/command-center/summary")
    @ResponseBody
    public Map<String,Object> commandCenterSummary() {
        var reports = labReportRepository.findAll();
        var samples = labSampleRepository.findAllByOrderByCreatedAtDesc();
        Map<String,Double> signals = new LinkedHashMap<>();
        signals.put("UNRECEIVED_SPECIMENS", samples.stream().filter(s -> "ORDERED".equalsIgnoreCase(s.getStatus())).count() * 1.0);
        signals.put("COLLECTION_BACKLOG", samples.stream().filter(s -> "ORDERED".equalsIgnoreCase(s.getStatus())).count() * 1.0);
        signals.put("UNVERIFIED_RESULTS", reports.stream().filter(r -> r.getStatus() == LabReportStatus.PENDING).count() * 1.0);
        signals.put("PENDING_PATHOLOGIST_SIGNOFF", reports.stream().filter(r -> r.getStatus() == LabReportStatus.COMPLETED).count() * 1.0);
        signals.put("UNSIGNED_REPORTS", reports.stream().filter(r -> r.getStatus() == LabReportStatus.PENDING).count() * 1.0);
        signals.put("REJECTION_REVIEW_PENDING", samples.stream().filter(s -> "REJECTED".equalsIgnoreCase(s.getStatus())).count() * 1.0);
        signals.put("REJECTED_SAMPLE_ESCALATION", samples.stream().filter(s -> "REJECTED".equalsIgnoreCase(s.getStatus())).count() * 1.0);
        signals.put("SHIFT_COVERAGE_GAP", 0.0);
        LabDashboardSignalSnapshot snapshot = new LabDashboardSignalSnapshot(signals, Instant.now());
        Map<String,Object> result = new LinkedHashMap<>(commandCenter.summary(snapshot));
        result.put("reportCount", reports.size());
        result.put("sampleCount", samples.size());
        result.put("pendingReports", reports.stream().filter(r -> r.getStatus() == LabReportStatus.PENDING).count());
        result.put("completedReports", reports.stream().filter(r -> r.getStatus() == LabReportStatus.COMPLETED).count());
        result.put("dataNotice", "Live report and specimen counts are connected. Other workstreams report signal coverage gaps until their source systems are integrated.");
        return result;
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
