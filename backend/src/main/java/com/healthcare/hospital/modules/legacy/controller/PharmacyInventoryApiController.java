package com.healthcare.hospital.modules.legacy.controller;

import com.healthcare.hospital.modules.legacy.service.PharmacyReorderService;
import com.healthcare.hospital.shared.dto.MedicineReorderRecommendationDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/inventory")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyInventoryApiController {

    private final PharmacyReorderService reorderService;

    public PharmacyInventoryApiController(PharmacyReorderService reorderService) {
        this.reorderService = reorderService;
    }

    @GetMapping("/reorder-recommendations")
    public List<MedicineReorderRecommendationDto> reorderRecommendations() {
        return reorderService.getRecommendations();
    }
}
