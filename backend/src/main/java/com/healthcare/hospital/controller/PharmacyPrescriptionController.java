package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.DispenseRequestDto;
import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineBatch;
import com.healthcare.hospital.entity.Prescription;
import com.healthcare.hospital.entity.PrescriptionDispensation;
import com.healthcare.hospital.enums.DispensationStatus;
import com.healthcare.hospital.service.PharmacyBatchService;
import com.healthcare.hospital.service.PharmacyDispensationService;
import com.healthcare.hospital.service.PharmacyInventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pharmacy")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyPrescriptionController {

    private final PharmacyDispensationService dispensationService;
    private final PharmacyInventoryService inventoryService;
    private final PharmacyBatchService batchService;

    public PharmacyPrescriptionController(PharmacyDispensationService dispensationService,
                                          PharmacyInventoryService inventoryService,
                                          PharmacyBatchService batchService) {
        this.dispensationService = dispensationService;
        this.inventoryService = inventoryService;
        this.batchService = batchService;
    }

    @GetMapping("/prescriptions")
    public String prescriptionQueue(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Page<Prescription> prescriptionsPage = dispensationService.searchPrescriptions(query, status, page, size);
        model.addAttribute("prescriptionsPage", prescriptionsPage);
        model.addAttribute("query", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activeNav", "pharmacy-prescriptions");
        return "pharmacy/prescriptions";
    }

    @GetMapping("/prescriptions/{id}")
    public String viewPrescriptionDispenseScreen(@PathVariable Long id, Model model) {
        Prescription rx = dispensationService.getPrescriptionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));

        // Find potential medicine matches by medication name
        List<Medicine> allMedicines = inventoryService.getActiveMedicines();
        Optional<Medicine> directMatch = allMedicines.stream()
                .filter(m -> rx.getMedicationName().toLowerCase().contains(m.getName().toLowerCase()) ||
                             m.getName().toLowerCase().contains(rx.getMedicationName().toLowerCase()))
                .findFirst();

        Medicine selectedMedicine = directMatch.orElse(allMedicines.isEmpty() ? null : allMedicines.get(0));

        List<MedicineBatch> eligibleBatches = List.of();
        if (selectedMedicine != null) {
            eligibleBatches = batchService.getEligibleBatchesFEFO(selectedMedicine);
        }

        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(rx.getId());
        if (selectedMedicine != null) {
            dto.setMedicineId(selectedMedicine.getId());
        }

        model.addAttribute("prescription", rx);
        model.addAttribute("selectedMedicine", selectedMedicine);
        model.addAttribute("eligibleBatches", eligibleBatches);
        model.addAttribute("medicines", allMedicines);
        model.addAttribute("dispenseDto", dto);
        model.addAttribute("pastDispensations", dispensationService.getDispensationsForPrescription(rx));
        model.addAttribute("activeNav", "pharmacy-prescriptions");
        return "pharmacy/dispense";
    }

    @PostMapping("/prescriptions/{id}/dispense")
    public String processDispensation(
            @PathVariable Long id,
            @Valid @ModelAttribute("dispenseDto") DispenseRequestDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Prescription rx = dispensationService.getPrescriptionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));

        if (bindingResult.hasErrors() || dto.getAllocations().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid dispensing request. Please select at least one batch and positive quantity.");
            return "redirect:/pharmacy/prescriptions/" + id;
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            PrescriptionDispensation dispensation = dispensationService.dispensePrescription(dto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Prescription successfully dispensed! Dispensation token: %s (%d units).",
                            dispensation.getDispensationNumber(), dispensation.getTotalQuantity()));
            return "redirect:/pharmacy/dispensations/" + dispensation.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/pharmacy/prescriptions/" + id;
        }
    }

    @GetMapping("/dispensations")
    public String listDispensations(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) DispensationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<PrescriptionDispensation> dispensationsPage = dispensationService.searchDispensations(query, status, start, end, page, size);
        model.addAttribute("dispensationsPage", dispensationsPage);
        model.addAttribute("query", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statuses", DispensationStatus.values());
        model.addAttribute("activeNav", "pharmacy-dispensations");
        return "pharmacy/dispensations";
    }

    @GetMapping("/dispensations/{id}")
    public String viewDispensationDetail(@PathVariable Long id, Model model) {
        PrescriptionDispensation dispensation = dispensationService.getDispensationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensation record not found: " + id));

        model.addAttribute("dispensation", dispensation);
        model.addAttribute("items", dispensation.getItems());
        model.addAttribute("activeNav", "pharmacy-dispensations");
        return "pharmacy/dispensation-detail";
    }
}
