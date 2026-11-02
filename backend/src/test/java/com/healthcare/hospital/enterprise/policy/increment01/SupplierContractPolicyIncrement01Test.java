package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.SupplierContractPolicy;

class SupplierContractPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus(null));
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus(""));
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus(" "));
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus("new"));
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", SupplierContractPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", SupplierContractPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", SupplierContractPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", SupplierContractPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", SupplierContractPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", SupplierContractPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", SupplierContractPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", SupplierContractPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", SupplierContractPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", SupplierContractPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", SupplierContractPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", SupplierContractPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", SupplierContractPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", SupplierContractPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", SupplierContractPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", SupplierContractPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", SupplierContractPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", SupplierContractPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", SupplierContractPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", SupplierContractPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, SupplierContractPolicy.pageSize(-100));
        assertEquals(25, SupplierContractPolicy.pageSize(-1));
        assertEquals(25, SupplierContractPolicy.pageSize(0));
        assertEquals(1, SupplierContractPolicy.pageSize(1));
        assertEquals(2, SupplierContractPolicy.pageSize(2));
        assertEquals(5, SupplierContractPolicy.pageSize(5));
        assertEquals(10, SupplierContractPolicy.pageSize(10));
        assertEquals(20, SupplierContractPolicy.pageSize(20));
        assertEquals(24, SupplierContractPolicy.pageSize(24));
        assertEquals(25, SupplierContractPolicy.pageSize(25));
        assertEquals(26, SupplierContractPolicy.pageSize(26));
        assertEquals(49, SupplierContractPolicy.pageSize(49));
        assertEquals(50, SupplierContractPolicy.pageSize(50));
        assertEquals(75, SupplierContractPolicy.pageSize(75));
        assertEquals(99, SupplierContractPolicy.pageSize(99));
        assertEquals(100, SupplierContractPolicy.pageSize(100));
        assertEquals(101, SupplierContractPolicy.pageSize(101));
        assertEquals(150, SupplierContractPolicy.pageSize(150));
        assertEquals(199, SupplierContractPolicy.pageSize(199));
        assertEquals(200, SupplierContractPolicy.pageSize(200));
        assertEquals(200, SupplierContractPolicy.pageSize(201));
        assertEquals(200, SupplierContractPolicy.pageSize(250));
        assertEquals(200, SupplierContractPolicy.pageSize(500));
        assertEquals(200, SupplierContractPolicy.pageSize(1000));
        assertEquals(200, SupplierContractPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, SupplierContractPolicy.pageIndex(-100));
        assertEquals(0, SupplierContractPolicy.pageIndex(-10));
        assertEquals(0, SupplierContractPolicy.pageIndex(-2));
        assertEquals(0, SupplierContractPolicy.pageIndex(-1));
        assertEquals(0, SupplierContractPolicy.pageIndex(0));
        assertEquals(1, SupplierContractPolicy.pageIndex(1));
        assertEquals(2, SupplierContractPolicy.pageIndex(2));
        assertEquals(3, SupplierContractPolicy.pageIndex(3));
        assertEquals(4, SupplierContractPolicy.pageIndex(4));
        assertEquals(5, SupplierContractPolicy.pageIndex(5));
        assertEquals(6, SupplierContractPolicy.pageIndex(6));
        assertEquals(7, SupplierContractPolicy.pageIndex(7));
        assertEquals(8, SupplierContractPolicy.pageIndex(8));
        assertEquals(9, SupplierContractPolicy.pageIndex(9));
        assertEquals(10, SupplierContractPolicy.pageIndex(10));
        assertEquals(11, SupplierContractPolicy.pageIndex(11));
        assertEquals(12, SupplierContractPolicy.pageIndex(12));
        assertEquals(13, SupplierContractPolicy.pageIndex(13));
        assertEquals(14, SupplierContractPolicy.pageIndex(14));
        assertEquals(15, SupplierContractPolicy.pageIndex(15));
        assertEquals(20, SupplierContractPolicy.pageIndex(20));
        assertEquals(50, SupplierContractPolicy.pageIndex(50));
        assertEquals(100, SupplierContractPolicy.pageIndex(100));
        assertEquals(1000, SupplierContractPolicy.pageIndex(1000));
        assertEquals(99999, SupplierContractPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", SupplierContractPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", SupplierContractPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", SupplierContractPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", SupplierContractPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", SupplierContractPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", SupplierContractPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", SupplierContractPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", SupplierContractPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", SupplierContractPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", SupplierContractPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", SupplierContractPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", SupplierContractPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", SupplierContractPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", SupplierContractPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", SupplierContractPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", SupplierContractPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", SupplierContractPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", SupplierContractPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", SupplierContractPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", SupplierContractPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", SupplierContractPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", SupplierContractPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", SupplierContractPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", SupplierContractPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", SupplierContractPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", SupplierContractPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", SupplierContractPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", SupplierContractPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", SupplierContractPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", SupplierContractPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", SupplierContractPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", SupplierContractPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", SupplierContractPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", SupplierContractPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", SupplierContractPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", SupplierContractPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", SupplierContractPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", SupplierContractPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", SupplierContractPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", SupplierContractPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", SupplierContractPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", SupplierContractPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", SupplierContractPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", SupplierContractPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", SupplierContractPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", SupplierContractPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", SupplierContractPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", SupplierContractPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", SupplierContractPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", SupplierContractPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", SupplierContractPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", SupplierContractPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", SupplierContractPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", SupplierContractPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", SupplierContractPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", SupplierContractPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", SupplierContractPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", SupplierContractPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", SupplierContractPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", SupplierContractPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", SupplierContractPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", SupplierContractPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", SupplierContractPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", SupplierContractPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", SupplierContractPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", SupplierContractPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", SupplierContractPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", SupplierContractPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", SupplierContractPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", SupplierContractPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", SupplierContractPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", SupplierContractPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", SupplierContractPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", SupplierContractPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", SupplierContractPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, SupplierContractPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, SupplierContractPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, SupplierContractPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", SupplierContractPolicy.normalizePriority("critical"));
        assertEquals("URGENT", SupplierContractPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", SupplierContractPolicy.normalizePriority("high"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("normal"));
        assertEquals("LOW", SupplierContractPolicy.normalizePriority("low"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority(null));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority(""));
        assertEquals("CRITICAL", SupplierContractPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", SupplierContractPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", SupplierContractPolicy.normalizePriority("low "));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", SupplierContractPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("none"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, SupplierContractPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", SupplierContractPolicy.redactForLog(""));
        assertEquals("****", SupplierContractPolicy.redactForLog("a"));
        assertEquals("****", SupplierContractPolicy.redactForLog("ab"));
        assertEquals("****", SupplierContractPolicy.redactForLog("abc"));
        assertEquals("****", SupplierContractPolicy.redactForLog("abcd"));
        assertEquals("ab***de", SupplierContractPolicy.redactForLog("abcde"));
        assertEquals("pa***23", SupplierContractPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", SupplierContractPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", SupplierContractPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", SupplierContractPolicy.redactForLog("Rahul"));
        assertEquals("do***01", SupplierContractPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", SupplierContractPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", SupplierContractPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", SupplierContractPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", SupplierContractPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", SupplierContractPolicy.redactForLog("test-value"));
        assertEquals("se***ef", SupplierContractPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", SupplierContractPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", SupplierContractPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", SupplierContractPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", SupplierContractPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", SupplierContractPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", SupplierContractPolicy.redactForLog("case-2026"));
        assertEquals("re***88", SupplierContractPolicy.redactForLog("record-88"));
        assertEquals("ad***77", SupplierContractPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", SupplierContractPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", SupplierContractPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", SupplierContractPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", SupplierContractPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", SupplierContractPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", SupplierContractPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", SupplierContractPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", SupplierContractPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", SupplierContractPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", SupplierContractPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", SupplierContractPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", SupplierContractPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", SupplierContractPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", SupplierContractPolicy.truncate("Access Review", 5));
        assertEquals("War…", SupplierContractPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", SupplierContractPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", SupplierContractPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", SupplierContractPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", SupplierContractPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", SupplierContractPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", SupplierContractPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", SupplierContractPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", SupplierContractPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", SupplierContractPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", SupplierContractPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(SupplierContractPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(SupplierContractPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(SupplierContractPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(SupplierContractPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(SupplierContractPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, SupplierContractPolicy.requiresApproval(null, null));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, SupplierContractPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, SupplierContractPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
