package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.DispensingSummaryDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.service.PharmacyAuditService;
import com.healthcare.hospital.service.PharmacyBatchService;
import com.healthcare.hospital.service.PharmacyReportService;
import com.healthcare.hospital.service.PharmacySupplierService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/pharmacy")
@PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
public class PharmacyReportController {

    private final PharmacyReportService reportService;
    private final PharmacyBatchService batchService;
    private final PharmacySupplierService supplierService;
    private final PharmacyAuditService auditService;

    public PharmacyReportController(PharmacyReportService reportService,
                                    PharmacyBatchService batchService,
                                    PharmacySupplierService supplierService,
                                    PharmacyAuditService auditService) {
        this.reportService = reportService;
        this.batchService = batchService;
        this.supplierService = supplierService;
        this.auditService = auditService;
    }

    @GetMapping("/reports")
    public String reportsHub(
            @RequestParam(defaultValue = "inventory") String tab,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long supplierId,
            Model model
    ) {
        LocalDate now = LocalDate.now();
        LocalDate effectiveStart = startDate != null ? startDate : now.minusDays(30);
        LocalDate effectiveEnd = endDate != null ? endDate : now;

        LocalDateTime startDateTime = effectiveStart.atStartOfDay();
        LocalDateTime endDateTime = effectiveEnd.atTime(LocalTime.MAX);

        if ("inventory".equalsIgnoreCase(tab)) {
            model.addAttribute("inventoryList", reportService.getCurrentInventory());
        } else if ("low-stock".equalsIgnoreCase(tab)) {
            model.addAttribute("lowStockList", reportService.getLowStockReport());
            model.addAttribute("outOfStockList", reportService.getOutOfStockReport());
        } else if ("movement".equalsIgnoreCase(tab)) {
            model.addAttribute("movementList", reportService.getStockMovementReport(startDateTime, endDateTime));
        } else if ("dispensing".equalsIgnoreCase(tab)) {
            List<DispensingSummaryDto> summaries = reportService.getMedicineDispensingSummaries(startDateTime, endDateTime);
            model.addAttribute("dispensingSummaries", summaries);
            model.addAttribute("dispensationsList", reportService.getDispensationsReport(startDateTime, endDateTime));
        } else if ("purchases".equalsIgnoreCase(tab)) {
            model.addAttribute("purchasesList", reportService.getPurchasesReport(effectiveStart, effectiveEnd, supplierId));
            model.addAttribute("suppliers", supplierService.getAllActiveSuppliers());
        }

        model.addAttribute("activeTab", tab);
        model.addAttribute("startDate", effectiveStart);
        model.addAttribute("endDate", effectiveEnd);
        model.addAttribute("selectedSupplierId", supplierId);
        model.addAttribute("activeNav", "pharmacy-reports");
        return "pharmacy/reports";
    }

    @GetMapping("/reports/inventory/export")
    public void exportInventoryCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=careflow_inventory_" + LocalDate.now() + ".csv");
        response.getWriter().write(reportService.exportInventoryCsv());
    }

    @GetMapping("/reports/movement/export")
    public void exportMovementCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response
    ) throws IOException {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock_movement_" + start + "_to_" + end + ".csv");
        response.getWriter().write(reportService.exportStockMovementCsv(start.atStartOfDay(), end.atTime(LocalTime.MAX)));
    }

    @GetMapping("/reports/dispensations/export")
    public void exportDispensationsCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response
    ) throws IOException {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dispensations_" + start + "_to_" + end + ".csv");
        response.getWriter().write(reportService.exportDispensationsCsv(start.atStartOfDay(), end.atTime(LocalTime.MAX)));
    }

    @GetMapping("/alerts/expiry")
    public String expiryAlerts(Model model) {
        List<MedicineBatch> expiredBatches = batchService.getExpiredBatches();
        List<MedicineBatch> nearExpiryBatches = batchService.getNearExpiryBatches(60);

        model.addAttribute("expiredBatches", expiredBatches);
        model.addAttribute("nearExpiryBatches", nearExpiryBatches);
        model.addAttribute("activeNav", "pharmacy-alerts");
        return "pharmacy/expiry-alerts";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<PharmacyAuditLog> auditLogsPage = auditService.searchLogs(query, start, end, page, size);
        model.addAttribute("auditLogsPage", auditLogsPage);
        model.addAttribute("query", query);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activeNav", "pharmacy-audit");
        return "pharmacy/audit-logs";
    }
}
