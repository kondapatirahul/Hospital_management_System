package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.PurchaseApprovalPolicy;

class PurchaseApprovalPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus(null));
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus(""));
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus(" "));
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus("new"));
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", PurchaseApprovalPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", PurchaseApprovalPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", PurchaseApprovalPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", PurchaseApprovalPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", PurchaseApprovalPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", PurchaseApprovalPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", PurchaseApprovalPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", PurchaseApprovalPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", PurchaseApprovalPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", PurchaseApprovalPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", PurchaseApprovalPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", PurchaseApprovalPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", PurchaseApprovalPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", PurchaseApprovalPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", PurchaseApprovalPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", PurchaseApprovalPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", PurchaseApprovalPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", PurchaseApprovalPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", PurchaseApprovalPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", PurchaseApprovalPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, PurchaseApprovalPolicy.pageSize(-100));
        assertEquals(25, PurchaseApprovalPolicy.pageSize(-1));
        assertEquals(25, PurchaseApprovalPolicy.pageSize(0));
        assertEquals(1, PurchaseApprovalPolicy.pageSize(1));
        assertEquals(2, PurchaseApprovalPolicy.pageSize(2));
        assertEquals(5, PurchaseApprovalPolicy.pageSize(5));
        assertEquals(10, PurchaseApprovalPolicy.pageSize(10));
        assertEquals(20, PurchaseApprovalPolicy.pageSize(20));
        assertEquals(24, PurchaseApprovalPolicy.pageSize(24));
        assertEquals(25, PurchaseApprovalPolicy.pageSize(25));
        assertEquals(26, PurchaseApprovalPolicy.pageSize(26));
        assertEquals(49, PurchaseApprovalPolicy.pageSize(49));
        assertEquals(50, PurchaseApprovalPolicy.pageSize(50));
        assertEquals(75, PurchaseApprovalPolicy.pageSize(75));
        assertEquals(99, PurchaseApprovalPolicy.pageSize(99));
        assertEquals(100, PurchaseApprovalPolicy.pageSize(100));
        assertEquals(101, PurchaseApprovalPolicy.pageSize(101));
        assertEquals(150, PurchaseApprovalPolicy.pageSize(150));
        assertEquals(199, PurchaseApprovalPolicy.pageSize(199));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(200));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(201));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(250));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(500));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(1000));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(-100));
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(-10));
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(-2));
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(-1));
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(0));
        assertEquals(1, PurchaseApprovalPolicy.pageIndex(1));
        assertEquals(2, PurchaseApprovalPolicy.pageIndex(2));
        assertEquals(3, PurchaseApprovalPolicy.pageIndex(3));
        assertEquals(4, PurchaseApprovalPolicy.pageIndex(4));
        assertEquals(5, PurchaseApprovalPolicy.pageIndex(5));
        assertEquals(6, PurchaseApprovalPolicy.pageIndex(6));
        assertEquals(7, PurchaseApprovalPolicy.pageIndex(7));
        assertEquals(8, PurchaseApprovalPolicy.pageIndex(8));
        assertEquals(9, PurchaseApprovalPolicy.pageIndex(9));
        assertEquals(10, PurchaseApprovalPolicy.pageIndex(10));
        assertEquals(11, PurchaseApprovalPolicy.pageIndex(11));
        assertEquals(12, PurchaseApprovalPolicy.pageIndex(12));
        assertEquals(13, PurchaseApprovalPolicy.pageIndex(13));
        assertEquals(14, PurchaseApprovalPolicy.pageIndex(14));
        assertEquals(15, PurchaseApprovalPolicy.pageIndex(15));
        assertEquals(20, PurchaseApprovalPolicy.pageIndex(20));
        assertEquals(50, PurchaseApprovalPolicy.pageIndex(50));
        assertEquals(100, PurchaseApprovalPolicy.pageIndex(100));
        assertEquals(1000, PurchaseApprovalPolicy.pageIndex(1000));
        assertEquals(99999, PurchaseApprovalPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", PurchaseApprovalPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PurchaseApprovalPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", PurchaseApprovalPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", PurchaseApprovalPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", PurchaseApprovalPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", PurchaseApprovalPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PurchaseApprovalPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PurchaseApprovalPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", PurchaseApprovalPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", PurchaseApprovalPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PurchaseApprovalPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", PurchaseApprovalPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", PurchaseApprovalPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PurchaseApprovalPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PurchaseApprovalPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PurchaseApprovalPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", PurchaseApprovalPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PurchaseApprovalPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", PurchaseApprovalPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", PurchaseApprovalPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", PurchaseApprovalPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PurchaseApprovalPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PurchaseApprovalPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PurchaseApprovalPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", PurchaseApprovalPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", PurchaseApprovalPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PurchaseApprovalPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", PurchaseApprovalPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", PurchaseApprovalPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", PurchaseApprovalPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", PurchaseApprovalPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PurchaseApprovalPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PurchaseApprovalPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", PurchaseApprovalPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", PurchaseApprovalPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PurchaseApprovalPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", PurchaseApprovalPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", PurchaseApprovalPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PurchaseApprovalPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PurchaseApprovalPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PurchaseApprovalPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", PurchaseApprovalPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PurchaseApprovalPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", PurchaseApprovalPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", PurchaseApprovalPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", PurchaseApprovalPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PurchaseApprovalPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PurchaseApprovalPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PurchaseApprovalPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", PurchaseApprovalPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", PurchaseApprovalPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", PurchaseApprovalPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", PurchaseApprovalPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", PurchaseApprovalPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", PurchaseApprovalPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", PurchaseApprovalPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", PurchaseApprovalPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", PurchaseApprovalPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", PurchaseApprovalPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", PurchaseApprovalPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", PurchaseApprovalPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", PurchaseApprovalPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", PurchaseApprovalPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", PurchaseApprovalPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", PurchaseApprovalPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", PurchaseApprovalPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", PurchaseApprovalPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", PurchaseApprovalPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", PurchaseApprovalPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", PurchaseApprovalPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", PurchaseApprovalPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", PurchaseApprovalPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", PurchaseApprovalPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", PurchaseApprovalPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", PurchaseApprovalPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, PurchaseApprovalPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, PurchaseApprovalPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, PurchaseApprovalPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", PurchaseApprovalPolicy.normalizePriority("critical"));
        assertEquals("URGENT", PurchaseApprovalPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", PurchaseApprovalPolicy.normalizePriority("high"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("normal"));
        assertEquals("LOW", PurchaseApprovalPolicy.normalizePriority("low"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority(null));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority(""));
        assertEquals("CRITICAL", PurchaseApprovalPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", PurchaseApprovalPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", PurchaseApprovalPolicy.normalizePriority("low "));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", PurchaseApprovalPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("none"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, PurchaseApprovalPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", PurchaseApprovalPolicy.redactForLog(""));
        assertEquals("****", PurchaseApprovalPolicy.redactForLog("a"));
        assertEquals("****", PurchaseApprovalPolicy.redactForLog("ab"));
        assertEquals("****", PurchaseApprovalPolicy.redactForLog("abc"));
        assertEquals("****", PurchaseApprovalPolicy.redactForLog("abcd"));
        assertEquals("ab***de", PurchaseApprovalPolicy.redactForLog("abcde"));
        assertEquals("pa***23", PurchaseApprovalPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", PurchaseApprovalPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", PurchaseApprovalPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", PurchaseApprovalPolicy.redactForLog("Rahul"));
        assertEquals("do***01", PurchaseApprovalPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", PurchaseApprovalPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", PurchaseApprovalPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", PurchaseApprovalPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", PurchaseApprovalPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", PurchaseApprovalPolicy.redactForLog("test-value"));
        assertEquals("se***ef", PurchaseApprovalPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", PurchaseApprovalPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", PurchaseApprovalPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", PurchaseApprovalPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", PurchaseApprovalPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", PurchaseApprovalPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", PurchaseApprovalPolicy.redactForLog("case-2026"));
        assertEquals("re***88", PurchaseApprovalPolicy.redactForLog("record-88"));
        assertEquals("ad***77", PurchaseApprovalPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", PurchaseApprovalPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", PurchaseApprovalPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", PurchaseApprovalPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", PurchaseApprovalPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", PurchaseApprovalPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", PurchaseApprovalPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", PurchaseApprovalPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", PurchaseApprovalPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", PurchaseApprovalPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", PurchaseApprovalPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", PurchaseApprovalPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", PurchaseApprovalPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", PurchaseApprovalPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", PurchaseApprovalPolicy.truncate("Access Review", 5));
        assertEquals("War…", PurchaseApprovalPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", PurchaseApprovalPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", PurchaseApprovalPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", PurchaseApprovalPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", PurchaseApprovalPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", PurchaseApprovalPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", PurchaseApprovalPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", PurchaseApprovalPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", PurchaseApprovalPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", PurchaseApprovalPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", PurchaseApprovalPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(PurchaseApprovalPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(PurchaseApprovalPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(null, null));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, PurchaseApprovalPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
