package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.SupplierDto;
import com.healthcare.hospital.entity.Supplier;
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
@RequestMapping("/pharmacy/suppliers")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacySupplierController {

    private final PharmacySupplierService supplierService;

    public PharmacySupplierController(PharmacySupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listSuppliers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Page<Supplier> suppliersPage = supplierService.searchSuppliers(query, page, size);
        model.addAttribute("suppliersPage", suppliersPage);
        model.addAttribute("query", query);
        model.addAttribute("activeNav", "pharmacy-suppliers");
        return "pharmacy/suppliers";
    }

    @GetMapping("/new")
    public String newSupplierForm(Model model) {
        SupplierDto dto = new SupplierDto();
        dto.setSupplierCode(supplierService.generateSupplierCode());
        model.addAttribute("supplierDto", dto);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "pharmacy-suppliers");
        return "pharmacy/supplier-form";
    }

    @GetMapping("/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));

        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setSupplierCode(supplier.getSupplierCode());
        dto.setName(supplier.getName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setLicenseNumber(supplier.getLicenseNumber());
        dto.setPaymentTerms(supplier.getPaymentTerms());
        dto.setActive(supplier.isActive());
        dto.setNotes(supplier.getNotes());

        model.addAttribute("supplierDto", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeNav", "pharmacy-suppliers");
        return "pharmacy/supplier-form";
    }

    @PostMapping("/save")
    public String saveSupplier(
            @Valid @ModelAttribute("supplierDto") SupplierDto supplierDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", supplierDto.getId() != null);
            model.addAttribute("activeNav", "pharmacy-suppliers");
            return "pharmacy/supplier-form";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            Supplier saved = supplierService.saveSupplier(supplierDto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Supplier '" + saved.getName() + "' (" + saved.getSupplierCode() + ") saved successfully.");
            return "redirect:/pharmacy/suppliers";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", supplierDto.getId() != null);
            model.addAttribute("activeNav", "pharmacy-suppliers");
            return "pharmacy/supplier-form";
        }
    }
}
