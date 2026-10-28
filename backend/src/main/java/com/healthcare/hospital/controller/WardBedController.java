package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.WardBed;
import com.healthcare.hospital.entity.BedMovement;
import com.healthcare.hospital.repository.WardBedRepository;
import com.healthcare.hospital.repository.BedMovementRepository;
import com.healthcare.hospital.service.AuditEventService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
@RequestMapping("/staff/wards")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
@Validated
public class WardBedController {
    private static final Set<String> STATUSES = Set.of("AVAILABLE", "OCCUPIED", "CLEANING", "OUT_OF_SERVICE");
    private final WardBedRepository beds;
    private final AuditEventService audit;
    private final BedMovementRepository movements;

    public WardBedController(WardBedRepository beds, AuditEventService audit, BedMovementRepository movements) {
        this.beds = beds;
        this.audit = audit;
        this.movements = movements;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ward,
                        @RequestParam(required = false) String status, Model model) {
        var all = (ward != null && !ward.isBlank())
                ? beds.findByWardNameIgnoreCaseOrderByBedNumberAsc(ward.trim())
                : beds.findAllByOrderByWardNameAscBedNumberAsc();
        if (status != null && !status.isBlank()) all = all.stream()
                .filter(b -> status.equalsIgnoreCase(b.getStatus())).toList();
        model.addAttribute("beds", all);
        model.addAttribute("wardFilter", ward == null ? "" : ward);
        model.addAttribute("statusFilter", status == null ? "" : status);
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("totalBeds", beds.count());
        model.addAttribute("availableBeds", beds.countByStatus("AVAILABLE"));
        model.addAttribute("occupiedBeds", beds.countByStatus("OCCUPIED"));
        model.addAttribute("cleaningBeds", beds.countByStatus("CLEANING"));
        model.addAttribute("recentMovements", movements.findTop100ByOrderByChangedAtDesc().stream().limit(15).toList());
        return "staff/wards";
    }

    @PostMapping
    public String create(@RequestParam @NotBlank String wardName,
                         @RequestParam @NotBlank String bedNumber,
                         @RequestParam(defaultValue = "GENERAL") String bedType,
                         Principal principal, RedirectAttributes flash) {
        String ward = wardName.trim();
        String number = bedNumber.trim();
        if (ward.length() > 100 || number.length() > 40 || bedType.trim().length() > 40) {
            flash.addFlashAttribute("errorMessage", "Ward, bed number or type exceeds the allowed length.");
            return "redirect:/staff/wards";
        }
        if (beds.existsByWardNameIgnoreCaseAndBedNumberIgnoreCase(ward, number)) {
            flash.addFlashAttribute("errorMessage", "That bed number already exists in this ward.");
            return "redirect:/staff/wards";
        }
        WardBed bed = new WardBed();
        bed.setWardName(ward);
        bed.setBedNumber(number);
        bed.setBedType(bedType.trim().toUpperCase());
        bed.setStatus("AVAILABLE");
        bed.setLastStatusChange(LocalDateTime.now());
        beds.save(bed);
        audit.record(principal == null ? "system" : principal.getName(), "CREATE", "WardBed",
                ward + "/" + number, "Added bed to ward " + ward);
        flash.addFlashAttribute("successMessage", "Bed created and marked available.");
        return "redirect:/staff/wards";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam String status,
                               @RequestParam(required = false) String patientName,
                               @RequestParam(required = false) String patientIdentifier,
                               @RequestParam(required = false) String admissionNotes,
                               Principal principal, RedirectAttributes flash) {
        WardBed bed = beds.findById(id).orElse(null);
        String next = status == null ? "" : status.trim().toUpperCase();
        if (bed == null) {
            flash.addFlashAttribute("errorMessage", "Bed was not found.");
            return "redirect:/staff/wards";
        }
        if (!STATUSES.contains(next)) {
            flash.addFlashAttribute("errorMessage", "Unsupported bed status.");
            return "redirect:/staff/wards";
        }
        String previousStatus = bed.getStatus();
        if ("OCCUPIED".equals(next) && (patientName == null || patientName.isBlank())) {
            flash.addFlashAttribute("errorMessage", "Enter the patient name before assigning a bed.");
            return "redirect:/staff/wards";
        }
        if ("OCCUPIED".equals(next)) {
            bed.setPatientName(patientName.trim().substring(0, Math.min(patientName.trim().length(), 120)));
            bed.setPatientIdentifier(patientIdentifier == null ? null : patientIdentifier.trim().substring(0, Math.min(patientIdentifier.trim().trim().length(), 40)));
            bed.setAdmissionNotes(admissionNotes == null ? null : admissionNotes.trim().substring(0, Math.min(admissionNotes.trim().length(), 1500)));
            if (bed.getAssignedAt() == null) bed.setAssignedAt(LocalDateTime.now());
        } else if ("AVAILABLE".equals(next)) {
            bed.setPatientName(null);
            bed.setPatientIdentifier(null);
            bed.setAdmissionNotes(null);
            bed.setAssignedAt(null);
        }
        bed.setStatus(next);
        bed.setLastStatusChange(LocalDateTime.now());
        beds.save(bed);
        audit.record(principal == null ? "system" : principal.getName(), "STATUS_CHANGE", "WardBed",
                id, "Bed status changed to " + next);
        flash.addFlashAttribute("successMessage", "Bed status updated.");
        return "redirect:/staff/wards";
    }
}
