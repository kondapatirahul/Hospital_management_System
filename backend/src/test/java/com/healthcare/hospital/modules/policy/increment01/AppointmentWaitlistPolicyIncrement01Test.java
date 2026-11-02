package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.AppointmentWaitlistPolicy;

class AppointmentWaitlistPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus(null));
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus(""));
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus(" "));
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus("new"));
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", AppointmentWaitlistPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", AppointmentWaitlistPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", AppointmentWaitlistPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", AppointmentWaitlistPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", AppointmentWaitlistPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", AppointmentWaitlistPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", AppointmentWaitlistPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", AppointmentWaitlistPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", AppointmentWaitlistPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", AppointmentWaitlistPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", AppointmentWaitlistPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", AppointmentWaitlistPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", AppointmentWaitlistPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", AppointmentWaitlistPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", AppointmentWaitlistPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", AppointmentWaitlistPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", AppointmentWaitlistPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", AppointmentWaitlistPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", AppointmentWaitlistPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", AppointmentWaitlistPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, AppointmentWaitlistPolicy.pageSize(-100));
        assertEquals(25, AppointmentWaitlistPolicy.pageSize(-1));
        assertEquals(25, AppointmentWaitlistPolicy.pageSize(0));
        assertEquals(1, AppointmentWaitlistPolicy.pageSize(1));
        assertEquals(2, AppointmentWaitlistPolicy.pageSize(2));
        assertEquals(5, AppointmentWaitlistPolicy.pageSize(5));
        assertEquals(10, AppointmentWaitlistPolicy.pageSize(10));
        assertEquals(20, AppointmentWaitlistPolicy.pageSize(20));
        assertEquals(24, AppointmentWaitlistPolicy.pageSize(24));
        assertEquals(25, AppointmentWaitlistPolicy.pageSize(25));
        assertEquals(26, AppointmentWaitlistPolicy.pageSize(26));
        assertEquals(49, AppointmentWaitlistPolicy.pageSize(49));
        assertEquals(50, AppointmentWaitlistPolicy.pageSize(50));
        assertEquals(75, AppointmentWaitlistPolicy.pageSize(75));
        assertEquals(99, AppointmentWaitlistPolicy.pageSize(99));
        assertEquals(100, AppointmentWaitlistPolicy.pageSize(100));
        assertEquals(101, AppointmentWaitlistPolicy.pageSize(101));
        assertEquals(150, AppointmentWaitlistPolicy.pageSize(150));
        assertEquals(199, AppointmentWaitlistPolicy.pageSize(199));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(200));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(201));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(250));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(500));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(1000));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(-100));
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(-10));
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(-2));
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(-1));
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(0));
        assertEquals(1, AppointmentWaitlistPolicy.pageIndex(1));
        assertEquals(2, AppointmentWaitlistPolicy.pageIndex(2));
        assertEquals(3, AppointmentWaitlistPolicy.pageIndex(3));
        assertEquals(4, AppointmentWaitlistPolicy.pageIndex(4));
        assertEquals(5, AppointmentWaitlistPolicy.pageIndex(5));
        assertEquals(6, AppointmentWaitlistPolicy.pageIndex(6));
        assertEquals(7, AppointmentWaitlistPolicy.pageIndex(7));
        assertEquals(8, AppointmentWaitlistPolicy.pageIndex(8));
        assertEquals(9, AppointmentWaitlistPolicy.pageIndex(9));
        assertEquals(10, AppointmentWaitlistPolicy.pageIndex(10));
        assertEquals(11, AppointmentWaitlistPolicy.pageIndex(11));
        assertEquals(12, AppointmentWaitlistPolicy.pageIndex(12));
        assertEquals(13, AppointmentWaitlistPolicy.pageIndex(13));
        assertEquals(14, AppointmentWaitlistPolicy.pageIndex(14));
        assertEquals(15, AppointmentWaitlistPolicy.pageIndex(15));
        assertEquals(20, AppointmentWaitlistPolicy.pageIndex(20));
        assertEquals(50, AppointmentWaitlistPolicy.pageIndex(50));
        assertEquals(100, AppointmentWaitlistPolicy.pageIndex(100));
        assertEquals(1000, AppointmentWaitlistPolicy.pageIndex(1000));
        assertEquals(99999, AppointmentWaitlistPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", AppointmentWaitlistPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", AppointmentWaitlistPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", AppointmentWaitlistPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", AppointmentWaitlistPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", AppointmentWaitlistPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", AppointmentWaitlistPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", AppointmentWaitlistPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", AppointmentWaitlistPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", AppointmentWaitlistPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", AppointmentWaitlistPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", AppointmentWaitlistPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", AppointmentWaitlistPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", AppointmentWaitlistPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", AppointmentWaitlistPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", AppointmentWaitlistPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", AppointmentWaitlistPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", AppointmentWaitlistPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", AppointmentWaitlistPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", AppointmentWaitlistPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", AppointmentWaitlistPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", AppointmentWaitlistPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", AppointmentWaitlistPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", AppointmentWaitlistPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", AppointmentWaitlistPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", AppointmentWaitlistPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", AppointmentWaitlistPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", AppointmentWaitlistPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", AppointmentWaitlistPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", AppointmentWaitlistPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", AppointmentWaitlistPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", AppointmentWaitlistPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", AppointmentWaitlistPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", AppointmentWaitlistPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", AppointmentWaitlistPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", AppointmentWaitlistPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", AppointmentWaitlistPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", AppointmentWaitlistPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", AppointmentWaitlistPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", AppointmentWaitlistPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", AppointmentWaitlistPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", AppointmentWaitlistPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", AppointmentWaitlistPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", AppointmentWaitlistPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", AppointmentWaitlistPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", AppointmentWaitlistPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", AppointmentWaitlistPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", AppointmentWaitlistPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", AppointmentWaitlistPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", AppointmentWaitlistPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", AppointmentWaitlistPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", AppointmentWaitlistPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", AppointmentWaitlistPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", AppointmentWaitlistPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", AppointmentWaitlistPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", AppointmentWaitlistPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", AppointmentWaitlistPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", AppointmentWaitlistPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", AppointmentWaitlistPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", AppointmentWaitlistPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", AppointmentWaitlistPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", AppointmentWaitlistPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", AppointmentWaitlistPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", AppointmentWaitlistPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", AppointmentWaitlistPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", AppointmentWaitlistPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", AppointmentWaitlistPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", AppointmentWaitlistPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", AppointmentWaitlistPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", AppointmentWaitlistPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", AppointmentWaitlistPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", AppointmentWaitlistPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", AppointmentWaitlistPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", AppointmentWaitlistPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", AppointmentWaitlistPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", AppointmentWaitlistPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, AppointmentWaitlistPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, AppointmentWaitlistPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, AppointmentWaitlistPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", AppointmentWaitlistPolicy.normalizePriority("critical"));
        assertEquals("URGENT", AppointmentWaitlistPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", AppointmentWaitlistPolicy.normalizePriority("high"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("normal"));
        assertEquals("LOW", AppointmentWaitlistPolicy.normalizePriority("low"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority(null));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority(""));
        assertEquals("CRITICAL", AppointmentWaitlistPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", AppointmentWaitlistPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", AppointmentWaitlistPolicy.normalizePriority("low "));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", AppointmentWaitlistPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("none"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, AppointmentWaitlistPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", AppointmentWaitlistPolicy.redactForLog(""));
        assertEquals("****", AppointmentWaitlistPolicy.redactForLog("a"));
        assertEquals("****", AppointmentWaitlistPolicy.redactForLog("ab"));
        assertEquals("****", AppointmentWaitlistPolicy.redactForLog("abc"));
        assertEquals("****", AppointmentWaitlistPolicy.redactForLog("abcd"));
        assertEquals("ab***de", AppointmentWaitlistPolicy.redactForLog("abcde"));
        assertEquals("pa***23", AppointmentWaitlistPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", AppointmentWaitlistPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", AppointmentWaitlistPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", AppointmentWaitlistPolicy.redactForLog("Rahul"));
        assertEquals("do***01", AppointmentWaitlistPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", AppointmentWaitlistPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", AppointmentWaitlistPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", AppointmentWaitlistPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", AppointmentWaitlistPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", AppointmentWaitlistPolicy.redactForLog("test-value"));
        assertEquals("se***ef", AppointmentWaitlistPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", AppointmentWaitlistPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", AppointmentWaitlistPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", AppointmentWaitlistPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", AppointmentWaitlistPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", AppointmentWaitlistPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", AppointmentWaitlistPolicy.redactForLog("case-2026"));
        assertEquals("re***88", AppointmentWaitlistPolicy.redactForLog("record-88"));
        assertEquals("ad***77", AppointmentWaitlistPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", AppointmentWaitlistPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", AppointmentWaitlistPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", AppointmentWaitlistPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", AppointmentWaitlistPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", AppointmentWaitlistPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", AppointmentWaitlistPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", AppointmentWaitlistPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", AppointmentWaitlistPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", AppointmentWaitlistPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", AppointmentWaitlistPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", AppointmentWaitlistPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", AppointmentWaitlistPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", AppointmentWaitlistPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", AppointmentWaitlistPolicy.truncate("Access Review", 5));
        assertEquals("War…", AppointmentWaitlistPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", AppointmentWaitlistPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", AppointmentWaitlistPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", AppointmentWaitlistPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", AppointmentWaitlistPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", AppointmentWaitlistPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", AppointmentWaitlistPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", AppointmentWaitlistPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", AppointmentWaitlistPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", AppointmentWaitlistPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", AppointmentWaitlistPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(AppointmentWaitlistPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(AppointmentWaitlistPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(null, null));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, AppointmentWaitlistPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
