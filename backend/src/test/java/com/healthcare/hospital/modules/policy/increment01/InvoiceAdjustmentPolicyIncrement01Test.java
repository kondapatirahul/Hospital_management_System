package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.InvoiceAdjustmentPolicy;

class InvoiceAdjustmentPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus(null));
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus(""));
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus(" "));
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus("new"));
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", InvoiceAdjustmentPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", InvoiceAdjustmentPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", InvoiceAdjustmentPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", InvoiceAdjustmentPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", InvoiceAdjustmentPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", InvoiceAdjustmentPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", InvoiceAdjustmentPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", InvoiceAdjustmentPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", InvoiceAdjustmentPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", InvoiceAdjustmentPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", InvoiceAdjustmentPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", InvoiceAdjustmentPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", InvoiceAdjustmentPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", InvoiceAdjustmentPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", InvoiceAdjustmentPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", InvoiceAdjustmentPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", InvoiceAdjustmentPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", InvoiceAdjustmentPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", InvoiceAdjustmentPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", InvoiceAdjustmentPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, InvoiceAdjustmentPolicy.pageSize(-100));
        assertEquals(25, InvoiceAdjustmentPolicy.pageSize(-1));
        assertEquals(25, InvoiceAdjustmentPolicy.pageSize(0));
        assertEquals(1, InvoiceAdjustmentPolicy.pageSize(1));
        assertEquals(2, InvoiceAdjustmentPolicy.pageSize(2));
        assertEquals(5, InvoiceAdjustmentPolicy.pageSize(5));
        assertEquals(10, InvoiceAdjustmentPolicy.pageSize(10));
        assertEquals(20, InvoiceAdjustmentPolicy.pageSize(20));
        assertEquals(24, InvoiceAdjustmentPolicy.pageSize(24));
        assertEquals(25, InvoiceAdjustmentPolicy.pageSize(25));
        assertEquals(26, InvoiceAdjustmentPolicy.pageSize(26));
        assertEquals(49, InvoiceAdjustmentPolicy.pageSize(49));
        assertEquals(50, InvoiceAdjustmentPolicy.pageSize(50));
        assertEquals(75, InvoiceAdjustmentPolicy.pageSize(75));
        assertEquals(99, InvoiceAdjustmentPolicy.pageSize(99));
        assertEquals(100, InvoiceAdjustmentPolicy.pageSize(100));
        assertEquals(101, InvoiceAdjustmentPolicy.pageSize(101));
        assertEquals(150, InvoiceAdjustmentPolicy.pageSize(150));
        assertEquals(199, InvoiceAdjustmentPolicy.pageSize(199));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(200));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(201));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(250));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(500));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(1000));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(-100));
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(-10));
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(-2));
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(-1));
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(0));
        assertEquals(1, InvoiceAdjustmentPolicy.pageIndex(1));
        assertEquals(2, InvoiceAdjustmentPolicy.pageIndex(2));
        assertEquals(3, InvoiceAdjustmentPolicy.pageIndex(3));
        assertEquals(4, InvoiceAdjustmentPolicy.pageIndex(4));
        assertEquals(5, InvoiceAdjustmentPolicy.pageIndex(5));
        assertEquals(6, InvoiceAdjustmentPolicy.pageIndex(6));
        assertEquals(7, InvoiceAdjustmentPolicy.pageIndex(7));
        assertEquals(8, InvoiceAdjustmentPolicy.pageIndex(8));
        assertEquals(9, InvoiceAdjustmentPolicy.pageIndex(9));
        assertEquals(10, InvoiceAdjustmentPolicy.pageIndex(10));
        assertEquals(11, InvoiceAdjustmentPolicy.pageIndex(11));
        assertEquals(12, InvoiceAdjustmentPolicy.pageIndex(12));
        assertEquals(13, InvoiceAdjustmentPolicy.pageIndex(13));
        assertEquals(14, InvoiceAdjustmentPolicy.pageIndex(14));
        assertEquals(15, InvoiceAdjustmentPolicy.pageIndex(15));
        assertEquals(20, InvoiceAdjustmentPolicy.pageIndex(20));
        assertEquals(50, InvoiceAdjustmentPolicy.pageIndex(50));
        assertEquals(100, InvoiceAdjustmentPolicy.pageIndex(100));
        assertEquals(1000, InvoiceAdjustmentPolicy.pageIndex(1000));
        assertEquals(99999, InvoiceAdjustmentPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", InvoiceAdjustmentPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", InvoiceAdjustmentPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", InvoiceAdjustmentPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", InvoiceAdjustmentPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", InvoiceAdjustmentPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", InvoiceAdjustmentPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", InvoiceAdjustmentPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", InvoiceAdjustmentPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", InvoiceAdjustmentPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", InvoiceAdjustmentPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", InvoiceAdjustmentPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", InvoiceAdjustmentPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", InvoiceAdjustmentPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", InvoiceAdjustmentPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", InvoiceAdjustmentPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", InvoiceAdjustmentPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", InvoiceAdjustmentPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", InvoiceAdjustmentPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", InvoiceAdjustmentPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", InvoiceAdjustmentPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", InvoiceAdjustmentPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", InvoiceAdjustmentPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", InvoiceAdjustmentPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", InvoiceAdjustmentPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", InvoiceAdjustmentPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", InvoiceAdjustmentPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", InvoiceAdjustmentPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", InvoiceAdjustmentPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", InvoiceAdjustmentPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", InvoiceAdjustmentPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", InvoiceAdjustmentPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", InvoiceAdjustmentPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", InvoiceAdjustmentPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", InvoiceAdjustmentPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", InvoiceAdjustmentPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", InvoiceAdjustmentPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", InvoiceAdjustmentPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", InvoiceAdjustmentPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", InvoiceAdjustmentPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", InvoiceAdjustmentPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", InvoiceAdjustmentPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", InvoiceAdjustmentPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", InvoiceAdjustmentPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", InvoiceAdjustmentPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", InvoiceAdjustmentPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", InvoiceAdjustmentPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", InvoiceAdjustmentPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", InvoiceAdjustmentPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", InvoiceAdjustmentPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", InvoiceAdjustmentPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", InvoiceAdjustmentPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", InvoiceAdjustmentPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", InvoiceAdjustmentPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", InvoiceAdjustmentPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", InvoiceAdjustmentPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", InvoiceAdjustmentPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", InvoiceAdjustmentPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", InvoiceAdjustmentPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", InvoiceAdjustmentPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", InvoiceAdjustmentPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", InvoiceAdjustmentPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", InvoiceAdjustmentPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", InvoiceAdjustmentPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", InvoiceAdjustmentPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", InvoiceAdjustmentPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", InvoiceAdjustmentPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", InvoiceAdjustmentPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", InvoiceAdjustmentPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", InvoiceAdjustmentPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", InvoiceAdjustmentPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", InvoiceAdjustmentPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", InvoiceAdjustmentPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", InvoiceAdjustmentPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", InvoiceAdjustmentPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", InvoiceAdjustmentPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, InvoiceAdjustmentPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, InvoiceAdjustmentPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, InvoiceAdjustmentPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", InvoiceAdjustmentPolicy.normalizePriority("critical"));
        assertEquals("URGENT", InvoiceAdjustmentPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", InvoiceAdjustmentPolicy.normalizePriority("high"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("normal"));
        assertEquals("LOW", InvoiceAdjustmentPolicy.normalizePriority("low"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority(null));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority(""));
        assertEquals("CRITICAL", InvoiceAdjustmentPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", InvoiceAdjustmentPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", InvoiceAdjustmentPolicy.normalizePriority("low "));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", InvoiceAdjustmentPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("none"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, InvoiceAdjustmentPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", InvoiceAdjustmentPolicy.redactForLog(""));
        assertEquals("****", InvoiceAdjustmentPolicy.redactForLog("a"));
        assertEquals("****", InvoiceAdjustmentPolicy.redactForLog("ab"));
        assertEquals("****", InvoiceAdjustmentPolicy.redactForLog("abc"));
        assertEquals("****", InvoiceAdjustmentPolicy.redactForLog("abcd"));
        assertEquals("ab***de", InvoiceAdjustmentPolicy.redactForLog("abcde"));
        assertEquals("pa***23", InvoiceAdjustmentPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", InvoiceAdjustmentPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", InvoiceAdjustmentPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", InvoiceAdjustmentPolicy.redactForLog("Rahul"));
        assertEquals("do***01", InvoiceAdjustmentPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", InvoiceAdjustmentPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", InvoiceAdjustmentPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", InvoiceAdjustmentPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", InvoiceAdjustmentPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", InvoiceAdjustmentPolicy.redactForLog("test-value"));
        assertEquals("se***ef", InvoiceAdjustmentPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", InvoiceAdjustmentPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", InvoiceAdjustmentPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", InvoiceAdjustmentPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", InvoiceAdjustmentPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", InvoiceAdjustmentPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", InvoiceAdjustmentPolicy.redactForLog("case-2026"));
        assertEquals("re***88", InvoiceAdjustmentPolicy.redactForLog("record-88"));
        assertEquals("ad***77", InvoiceAdjustmentPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", InvoiceAdjustmentPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", InvoiceAdjustmentPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", InvoiceAdjustmentPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", InvoiceAdjustmentPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", InvoiceAdjustmentPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", InvoiceAdjustmentPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", InvoiceAdjustmentPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", InvoiceAdjustmentPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", InvoiceAdjustmentPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", InvoiceAdjustmentPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", InvoiceAdjustmentPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", InvoiceAdjustmentPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", InvoiceAdjustmentPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", InvoiceAdjustmentPolicy.truncate("Access Review", 5));
        assertEquals("War…", InvoiceAdjustmentPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", InvoiceAdjustmentPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", InvoiceAdjustmentPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", InvoiceAdjustmentPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", InvoiceAdjustmentPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", InvoiceAdjustmentPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", InvoiceAdjustmentPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", InvoiceAdjustmentPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", InvoiceAdjustmentPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", InvoiceAdjustmentPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", InvoiceAdjustmentPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(null, null));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, InvoiceAdjustmentPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
