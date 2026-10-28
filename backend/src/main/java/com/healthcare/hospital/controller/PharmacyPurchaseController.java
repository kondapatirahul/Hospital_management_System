package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.dto.GoodsReceivedDto;
import com.healthcare.hospital.dto.GoodsReceivedItemDto;
import com.healthcare.hospital.dto.PurchaseOrderDto;
import com.healthcare.hospital.dto.PurchaseOrderItemDto;
import com.healthcare.hospital.entity.GoodsReceivedNote;
import com.healthcare.hospital.entity.PurchaseOrder;
import com.healthcare.hospital.entity.PurchaseOrderItem;
import com.healthcare.hospital.enums.PurchaseOrderStatus;
import com.healthcare.hospital.service.PharmacyInventoryService;
import com.healthcare.hospital.service.PharmacyPurchaseService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pharmacy/purchases")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyPurchaseController {

    private final PharmacyPurchaseService purchaseService;
    private final PharmacySupplierService supplierService;
    private final PharmacyInventoryService inventoryService;

    public PharmacyPurchaseController(PharmacyPurchaseService purchaseService,
                                      PharmacySupplierService supplierService,
                                      PharmacyInventoryService inventoryService) {
        this.purchaseService = purchaseService;
        this.supplierService = supplierService;
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String listPurchaseOrders(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) PurchaseOrderStatus status,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Page<PurchaseOrder> poPage = purchaseService.searchPurchaseOrders(query, status, supplierId, page, size);
        model.addAttribute("poPage", poPage);
        model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("query", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSupplierId", supplierId);
        model.addAttribute("activeNav", "pharmacy-purchases");
        return "pharmacy/purchases";
    }

    @GetMapping("/new")
    public String newPurchaseOrderForm(Model model) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setPoNumber(purchaseService.generatePoNumber());
        // pre-populate with 1 empty item line
        dto.getItems().add(new PurchaseOrderItemDto());

        model.addAttribute("poDto", dto);
        model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
        model.addAttribute("medicines", inventoryService.getActiveMedicines());
        model.addAttribute("activeNav", "pharmacy-purchases");
        return "pharmacy/purchase-form";
    }

    @PostMapping("/save")
    public String savePurchaseOrder(
            @Valid @ModelAttribute("poDto") PurchaseOrderDto poDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Filter out empty rows if any
        poDto.getItems().removeIf(i -> i.getMedicineId() == null || i.getOrderedQuantity() == null || i.getOrderedQuantity() <= 0);

        if (poDto.getItems().isEmpty()) {
            bindingResult.rejectValue("items", "empty", "At least one valid item is required");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("activeNav", "pharmacy-purchases");
            return "pharmacy/purchase-form";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            PurchaseOrder savedPo = purchaseService.createPurchaseOrder(poDto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Purchase order " + savedPo.getPoNumber() + " created successfully.");
            return "redirect:/pharmacy/purchases/" + savedPo.getId();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
            model.addAttribute("medicines", inventoryService.getActiveMedicines());
            model.addAttribute("activeNav", "pharmacy-purchases");
            return "pharmacy/purchase-form";
        }
    }

    @GetMapping("/{id}")
    public String viewPurchaseOrderDetail(@PathVariable Long id, Model model) {
        PurchaseOrder po = purchaseService.getPurchaseOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + id));

        List<GoodsReceivedNote> grns = purchaseService.getGrnsForPurchaseOrder(po);
        model.addAttribute("po", po);
        model.addAttribute("grns", grns);
        model.addAttribute("activeNav", "pharmacy-purchases");
        return "pharmacy/purchase-detail";
    }

    @GetMapping("/{id}/receive")
    public String receiveGoodsForm(@PathVariable Long id, Model model) {
        PurchaseOrder po = purchaseService.getPurchaseOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + id));

        if (po.getStatus() == PurchaseOrderStatus.RECEIVED || po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("PO is already completed or cancelled: " + po.getStatus());
        }

        GoodsReceivedDto grnDto = new GoodsReceivedDto();
        grnDto.setPurchaseOrderId(po.getId());
        grnDto.setReceivedDate(LocalDate.now());

        List<GoodsReceivedItemDto> items = new ArrayList<>();
        for (PurchaseOrderItem item : po.getItems()) {
            if (!item.isFullyReceived()) {
                GoodsReceivedItemDto itemDto = new GoodsReceivedItemDto();
                itemDto.setPurchaseOrderItemId(item.getId());
                itemDto.setMedicineId(item.getMedicine().getId());
                itemDto.setQuantityReceived(item.getPendingQuantity());
                itemDto.setUnitCost(item.getUnitCost());
                itemDto.setBatchNumber("BAT-" + item.getMedicine().getMedicineCode() + "-" + LocalDate.now().getYear());
                itemDto.setExpiryDate(LocalDate.now().plusYears(2));
                items.add(itemDto);
            }
        }
        grnDto.setItems(items);

        model.addAttribute("po", po);
        model.addAttribute("grnDto", grnDto);
        model.addAttribute("activeNav", "pharmacy-purchases");
        return "pharmacy/purchase-receive";
    }

    @PostMapping("/{id}/receive/save")
    public String processGoodsReceipt(
            @PathVariable Long id,
            @Valid @ModelAttribute("grnDto") GoodsReceivedDto grnDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        PurchaseOrder po = purchaseService.getPurchaseOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + id));

        if (bindingResult.hasErrors()) {
            model.addAttribute("po", po);
            model.addAttribute("activeNav", "pharmacy-purchases");
            return "pharmacy/purchase-receive";
        }

        try {
            String username = userDetails != null ? userDetails.getUsername() : "pharmacist";
            GoodsReceivedNote grn = purchaseService.processGoodsReceipt(grnDto, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Goods receipt note " + grn.getGrnNumber() + " processed! Inventory updated successfully.");
            return "redirect:/pharmacy/purchases/" + id;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("po", po);
            model.addAttribute("activeNav", "pharmacy-purchases");
            return "pharmacy/purchase-receive";
        }
    }
}
