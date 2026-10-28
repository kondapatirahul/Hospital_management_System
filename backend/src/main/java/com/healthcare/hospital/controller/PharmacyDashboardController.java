package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.PharmacyDashboardStatsDto;
import com.healthcare.hospital.service.PharmacyInventoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pharmacy")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyDashboardController {

    private final PharmacyInventoryService inventoryService;

    public PharmacyDashboardController(PharmacyInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String pharmacyRoot() {
        return "redirect:/pharmacy/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        PharmacyDashboardStatsDto stats = inventoryService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("activeNav", "pharmacy-dashboard");
        return "pharmacy/dashboard";
    }
}
