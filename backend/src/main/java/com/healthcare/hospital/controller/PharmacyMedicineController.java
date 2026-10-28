package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.MedicineDto;
import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineBatch;
import com.healthcare.hospital.service.PharmacyBatchService;
import com.healthcare.hospital.service.PharmacyInventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pharmacy/medicines")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyMedicineController {

    private final PharmacyInventoryService inventoryService;
    private final PharmacyBatchService batchService;

    public PharmacyMedicineController(PharmacyInventoryService inventoryService, PharmacyBatchService batchService) {
        this.inventoryService = inventoryService;
        this.batchService = batchService;
    }

    @GetMapping
    public String listMedicines(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        Page<Medicine> medicinesPage = inventoryService.searchMedicines(query, categoryId, status, page, size, sortField, sortDir);
        model.addAttribute("medicinesPage", medicinesPage);
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("query", query);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("activeNav", "pharmacy-medicines");
        return "pharmacy/medicines";
    }

    @GetMapping("/new")
    public String newMedicineForm(Model model) {
        MedicineDto dto = new MedicineDto();
        dto.setMedicineCode(inventoryService.generateNextMedicineCode());
        model.addAttribute("medicineDto", dto);
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "pharmacy-medicines");
        return "pharmacy/medicine-form";
    }

    @GetMapping("/edit/{id}")
    public String editMedicineForm(@PathVariable Long id, Model model) {
        Medicine medicine = inventoryService.getMedicineById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + id));

        MedicineDto dto = new MedicineDto();
        dto.setId(medicine.getId());
        dto.setMedicineCode(medicine.getMedicineCode());
        dto.setName(medicine.getName());
        dto.setGenericName(medicine.getGenericName());
        dto.setCategoryId(medicine.getCategory().getId());
        dto.setDosageForm(medicine.getDosageForm());
        dto.setStrength(medicine.getStrength());
        dto.setUnitOfMeasure(medicine.getUnitOfMeasure());
        dto.setReorderLevel(medicine.getReorderLevel());
        dto.setUnitPrice(medicine.getUnitPrice());
        dto.setPurchasePrice(medicine.getPurchasePrice());
        dto.setManufacturer(medicine.getManufacturer());
        dto.setStorageConditions(medicine.getStorageConditions());
        dto.setPrescriptionRequired(medicine.isPrescriptionRequired());
        dto.setActive(medicine.isActive());
        dto.setDescription(medicine.getDescription());

        model.addAttribute("medicineDto", dto);
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeNav", "pharmacy-medicines");
        return "pharmacy/medicine-form";
    }

    @PostMapping("/save")
    public String saveMedicine(
            @Valid @ModelAttribute("medicineDto") MedicineDto medicineDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", inventoryService.getAllCategories());
            model.addAttribute("isEdit", medicineDto.getId() != null);
            model.addAttribute("activeNav", "pharmacy-medicines");
            return "pharmacy/medicine-form";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            Medicine saved = inventoryService.saveMedicine(medicineDto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Medicine '" + saved.getName() + "' (" + saved.getMedicineCode() + ") saved successfully.");
            return "redirect:/pharmacy/medicines/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", inventoryService.getAllCategories());
            model.addAttribute("isEdit", medicineDto.getId() != null);
            model.addAttribute("activeNav", "pharmacy-medicines");
            return "pharmacy/medicine-form";
        }
    }

    @GetMapping("/{id}")
    public String viewMedicineDetail(@PathVariable Long id, Model model) {
        Medicine medicine = inventoryService.getMedicineById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + id));

        List<MedicineBatch> batches = batchService.getBatchesForMedicine(medicine);
        model.addAttribute("medicine", medicine);
        model.addAttribute("batches", batches);
        model.addAttribute("activeNav", "pharmacy-medicines");
        return "pharmacy/medicine-detail";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleMedicineStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
        inventoryService.toggleMedicineStatus(id, username);
        redirectAttributes.addFlashAttribute("successMessage", "Medicine status updated successfully.");
        return "redirect:/pharmacy/medicines/" + id;
    }
}
