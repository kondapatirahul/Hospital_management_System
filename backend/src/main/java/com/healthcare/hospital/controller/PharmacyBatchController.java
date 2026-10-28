package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.MedicineBatchDto;
import com.healthcare.hospital.dto.StockAdjustmentDto;
import com.healthcare.hospital.entity.MedicineBatch;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.StockAdjustmentReason;
import com.healthcare.hospital.service.PharmacyBatchService;
import com.healthcare.hospital.service.PharmacyInventoryService;
import com.healthcare.hospital.service.PharmacySupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pharmacy")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyBatchController {

    private final PharmacyBatchService batchService;
    private final PharmacyInventoryService inventoryService;
    private final PharmacySupplierService supplierService;

    public PharmacyBatchController(PharmacyBatchService batchService,
                                   PharmacyInventoryService inventoryService,
                                   PharmacySupplierService supplierService) {
        this.batchService = batchService;
        this.inventoryService = inventoryService;
        this.supplierService = supplierService;
    }

    @GetMapping("/batches")
    public String listBatches(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BatchStatus status,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Page<MedicineBatch> batchesPage = batchService.searchBatches(query, status, medicineId, page, size);
        model.addAttribute("batchesPage", batchesPage);
        model.addAttribute("medicines", inventoryService.getActiveMedicines());
        model.addAttribute("statuses", BatchStatus.values());
        model.addAttribute("query", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedMedicineId", medicineId);
        model.addAttribute("activeNav", "pharmacy-batches");
        return "pharmacy/batches";
    }

    @GetMapping("/stock/in")
    public String stockInForm(@RequestParam(required = false) Long medicineId, Model model) {
        MedicineBatchDto dto = new MedicineBatchDto();
        if (medicineId != null) {
            dto.setMedicineId(medicineId);
        }
        model.addAttribute("batchDto", dto);
        model.addAttribute("medicines", inventoryService.getActiveMedicines());
        model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
        model.addAttribute("activeNav", "pharmacy-stock-in");
        return "pharmacy/stock-in";
    }

    @PostMapping("/stock/in")
    public String processStockIn(
            @Valid @ModelAttribute("batchDto") MedicineBatchDto batchDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
            model.addAttribute("activeNav", "pharmacy-stock-in");
            return "pharmacy/stock-in";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            MedicineBatch savedBatch = batchService.createBatchAndStockIn(batchDto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Successfully stocked in %d units of %s into batch %s.",
                            batchDto.getQuantity(), savedBatch.getMedicine().getName(), savedBatch.getBatchNumber()));
            return "redirect:/pharmacy/medicines/" + savedBatch.getMedicine().getId();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
            model.addAttribute("activeNav", "pharmacy-stock-in");
            return "pharmacy/stock-in";
        }
    }

    @GetMapping("/stock/adjust")
    public String stockAdjustmentForm(@RequestParam(required = false) Long batchId, Model model) {
        StockAdjustmentDto dto = new StockAdjustmentDto();
        if (batchId != null) {
            dto.setBatchId(batchId);
            batchService.getBatchById(batchId).ifPresent(b -> model.addAttribute("selectedBatch", b));
        }
        model.addAttribute("adjustmentDto", dto);
        model.addAttribute("reasons", StockAdjustmentReason.values());
        model.addAttribute("medicines", inventoryService.getActiveMedicines());
        model.addAttribute("activeNav", "pharmacy-stock-adjust");
        return "pharmacy/stock-adjust";
    }

    @PostMapping("/stock/adjust")
    public String processStockAdjustment(
            @Valid @ModelAttribute("adjustmentDto") StockAdjustmentDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reasons", StockAdjustmentReason.values());
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("activeNav", "pharmacy-stock-adjust");
            return "pharmacy/stock-adjust";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            batchService.adjustStock(dto, username);
            redirectAttributes.addFlashAttribute("successMessage", "Stock adjustment completed successfully.");
            return "redirect:/pharmacy/batches";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("reasons", StockAdjustmentReason.values());
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("activeNav", "pharmacy-stock-adjust");
            return "pharmacy/stock-adjust";
        }
    }
}
