package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.PharmacyStocktakePolicy;

class PharmacyStocktakePolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus(null));
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus(""));
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus(" "));
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus("new"));
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", PharmacyStocktakePolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", PharmacyStocktakePolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", PharmacyStocktakePolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", PharmacyStocktakePolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", PharmacyStocktakePolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", PharmacyStocktakePolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", PharmacyStocktakePolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", PharmacyStocktakePolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", PharmacyStocktakePolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", PharmacyStocktakePolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", PharmacyStocktakePolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", PharmacyStocktakePolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", PharmacyStocktakePolicy.normalizeStatus("approved"));
        assertEquals("PENDING", PharmacyStocktakePolicy.normalizeStatus("pending"));
        assertEquals("OPEN", PharmacyStocktakePolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", PharmacyStocktakePolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", PharmacyStocktakePolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", PharmacyStocktakePolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", PharmacyStocktakePolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", PharmacyStocktakePolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, PharmacyStocktakePolicy.pageSize(-100));
        assertEquals(25, PharmacyStocktakePolicy.pageSize(-1));
        assertEquals(25, PharmacyStocktakePolicy.pageSize(0));
        assertEquals(1, PharmacyStocktakePolicy.pageSize(1));
        assertEquals(2, PharmacyStocktakePolicy.pageSize(2));
        assertEquals(5, PharmacyStocktakePolicy.pageSize(5));
        assertEquals(10, PharmacyStocktakePolicy.pageSize(10));
        assertEquals(20, PharmacyStocktakePolicy.pageSize(20));
        assertEquals(24, PharmacyStocktakePolicy.pageSize(24));
        assertEquals(25, PharmacyStocktakePolicy.pageSize(25));
        assertEquals(26, PharmacyStocktakePolicy.pageSize(26));
        assertEquals(49, PharmacyStocktakePolicy.pageSize(49));
        assertEquals(50, PharmacyStocktakePolicy.pageSize(50));
        assertEquals(75, PharmacyStocktakePolicy.pageSize(75));
        assertEquals(99, PharmacyStocktakePolicy.pageSize(99));
        assertEquals(100, PharmacyStocktakePolicy.pageSize(100));
        assertEquals(101, PharmacyStocktakePolicy.pageSize(101));
        assertEquals(150, PharmacyStocktakePolicy.pageSize(150));
        assertEquals(199, PharmacyStocktakePolicy.pageSize(199));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(200));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(201));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(250));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(500));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(1000));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(-100));
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(-10));
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(-2));
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(-1));
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(0));
        assertEquals(1, PharmacyStocktakePolicy.pageIndex(1));
        assertEquals(2, PharmacyStocktakePolicy.pageIndex(2));
        assertEquals(3, PharmacyStocktakePolicy.pageIndex(3));
        assertEquals(4, PharmacyStocktakePolicy.pageIndex(4));
        assertEquals(5, PharmacyStocktakePolicy.pageIndex(5));
        assertEquals(6, PharmacyStocktakePolicy.pageIndex(6));
        assertEquals(7, PharmacyStocktakePolicy.pageIndex(7));
        assertEquals(8, PharmacyStocktakePolicy.pageIndex(8));
        assertEquals(9, PharmacyStocktakePolicy.pageIndex(9));
        assertEquals(10, PharmacyStocktakePolicy.pageIndex(10));
        assertEquals(11, PharmacyStocktakePolicy.pageIndex(11));
        assertEquals(12, PharmacyStocktakePolicy.pageIndex(12));
        assertEquals(13, PharmacyStocktakePolicy.pageIndex(13));
        assertEquals(14, PharmacyStocktakePolicy.pageIndex(14));
        assertEquals(15, PharmacyStocktakePolicy.pageIndex(15));
        assertEquals(20, PharmacyStocktakePolicy.pageIndex(20));
        assertEquals(50, PharmacyStocktakePolicy.pageIndex(50));
        assertEquals(100, PharmacyStocktakePolicy.pageIndex(100));
        assertEquals(1000, PharmacyStocktakePolicy.pageIndex(1000));
        assertEquals(99999, PharmacyStocktakePolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", PharmacyStocktakePolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PharmacyStocktakePolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", PharmacyStocktakePolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", PharmacyStocktakePolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", PharmacyStocktakePolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", PharmacyStocktakePolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PharmacyStocktakePolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PharmacyStocktakePolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", PharmacyStocktakePolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", PharmacyStocktakePolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PharmacyStocktakePolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", PharmacyStocktakePolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", PharmacyStocktakePolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PharmacyStocktakePolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PharmacyStocktakePolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PharmacyStocktakePolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", PharmacyStocktakePolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PharmacyStocktakePolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", PharmacyStocktakePolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", PharmacyStocktakePolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", PharmacyStocktakePolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PharmacyStocktakePolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PharmacyStocktakePolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PharmacyStocktakePolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", PharmacyStocktakePolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", PharmacyStocktakePolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PharmacyStocktakePolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", PharmacyStocktakePolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", PharmacyStocktakePolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", PharmacyStocktakePolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", PharmacyStocktakePolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PharmacyStocktakePolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PharmacyStocktakePolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", PharmacyStocktakePolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", PharmacyStocktakePolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PharmacyStocktakePolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", PharmacyStocktakePolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", PharmacyStocktakePolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PharmacyStocktakePolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PharmacyStocktakePolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PharmacyStocktakePolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", PharmacyStocktakePolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PharmacyStocktakePolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", PharmacyStocktakePolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", PharmacyStocktakePolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", PharmacyStocktakePolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PharmacyStocktakePolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PharmacyStocktakePolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PharmacyStocktakePolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", PharmacyStocktakePolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", PharmacyStocktakePolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", PharmacyStocktakePolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", PharmacyStocktakePolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", PharmacyStocktakePolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", PharmacyStocktakePolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", PharmacyStocktakePolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", PharmacyStocktakePolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", PharmacyStocktakePolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", PharmacyStocktakePolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", PharmacyStocktakePolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", PharmacyStocktakePolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", PharmacyStocktakePolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", PharmacyStocktakePolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", PharmacyStocktakePolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", PharmacyStocktakePolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", PharmacyStocktakePolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", PharmacyStocktakePolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", PharmacyStocktakePolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", PharmacyStocktakePolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", PharmacyStocktakePolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", PharmacyStocktakePolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", PharmacyStocktakePolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", PharmacyStocktakePolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", PharmacyStocktakePolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", PharmacyStocktakePolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, PharmacyStocktakePolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, PharmacyStocktakePolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, PharmacyStocktakePolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", PharmacyStocktakePolicy.normalizePriority("critical"));
        assertEquals("URGENT", PharmacyStocktakePolicy.normalizePriority("urgent"));
        assertEquals("HIGH", PharmacyStocktakePolicy.normalizePriority("high"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("normal"));
        assertEquals("LOW", PharmacyStocktakePolicy.normalizePriority("low"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority(null));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority(""));
        assertEquals("CRITICAL", PharmacyStocktakePolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", PharmacyStocktakePolicy.normalizePriority("HIGH "));
        assertEquals("LOW", PharmacyStocktakePolicy.normalizePriority("low "));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("P1"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("P2"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("P3"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("routine"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("medium"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", PharmacyStocktakePolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("nil"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("none"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, PharmacyStocktakePolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", PharmacyStocktakePolicy.redactForLog(""));
        assertEquals("****", PharmacyStocktakePolicy.redactForLog("a"));
        assertEquals("****", PharmacyStocktakePolicy.redactForLog("ab"));
        assertEquals("****", PharmacyStocktakePolicy.redactForLog("abc"));
        assertEquals("****", PharmacyStocktakePolicy.redactForLog("abcd"));
        assertEquals("ab***de", PharmacyStocktakePolicy.redactForLog("abcde"));
        assertEquals("pa***23", PharmacyStocktakePolicy.redactForLog("patient-123"));
        assertEquals("IN***01", PharmacyStocktakePolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", PharmacyStocktakePolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", PharmacyStocktakePolicy.redactForLog("Rahul"));
        assertEquals("do***01", PharmacyStocktakePolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", PharmacyStocktakePolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", PharmacyStocktakePolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", PharmacyStocktakePolicy.redactForLog("CF-0001"));
        assertEquals("re***00", PharmacyStocktakePolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", PharmacyStocktakePolicy.redactForLog("test-value"));
        assertEquals("se***ef", PharmacyStocktakePolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", PharmacyStocktakePolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", PharmacyStocktakePolicy.redactForLog("MED-001"));
        assertEquals("ST***22", PharmacyStocktakePolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", PharmacyStocktakePolicy.redactForLog("patient-001"));
        assertEquals("bi***55", PharmacyStocktakePolicy.redactForLog("billing-55"));
        assertEquals("ca***26", PharmacyStocktakePolicy.redactForLog("case-2026"));
        assertEquals("re***88", PharmacyStocktakePolicy.redactForLog("record-88"));
        assertEquals("ad***77", PharmacyStocktakePolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", PharmacyStocktakePolicy.truncate("Patient Registration", 5));
        assertEquals("App…", PharmacyStocktakePolicy.truncate("Appointment", 4));
        assertEquals("Labor…", PharmacyStocktakePolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", PharmacyStocktakePolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", PharmacyStocktakePolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", PharmacyStocktakePolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", PharmacyStocktakePolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", PharmacyStocktakePolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", PharmacyStocktakePolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", PharmacyStocktakePolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", PharmacyStocktakePolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", PharmacyStocktakePolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", PharmacyStocktakePolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", PharmacyStocktakePolicy.truncate("Access Review", 5));
        assertEquals("War…", PharmacyStocktakePolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", PharmacyStocktakePolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", PharmacyStocktakePolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", PharmacyStocktakePolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", PharmacyStocktakePolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", PharmacyStocktakePolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", PharmacyStocktakePolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", PharmacyStocktakePolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", PharmacyStocktakePolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", PharmacyStocktakePolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", PharmacyStocktakePolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(PharmacyStocktakePolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(PharmacyStocktakePolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(null, null));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, PharmacyStocktakePolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
