package com.healthcare.hospital.enterprise.phase4.revenueplan.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Pure domain policy rules for revenue planning; kept separate for deterministic tests. */
public final class RevenuePlanPolicy {
 private RevenuePlanPolicy() {}
 public static final List<String> STATUS_ORDER=List.of("DRAFT", "REVIEW", "APPROVED", "ACTIVE", "ON_HOLD", "CLOSED");
 public static boolean isKnownStatus(String status) { return status!=null&&STATUS_ORDER.contains(status.trim().toUpperCase(Locale.ROOT)); }
 public static boolean isTerminal(String status) { return status!=null&&Set.of("CLOSED").contains(status.trim().toUpperCase(Locale.ROOT)); }
 public static long daysUntil(LocalDate date) { return date==null?Long.MAX_VALUE:ChronoUnit.DAYS.between(LocalDate.now(),date); }
 public static String urgency(LocalDate date) { long days=daysUntil(date); if(days<0)return "OVERDUE"; if(days<=2)return "CRITICAL"; if(days<=7)return "HIGH"; if(days<=30)return "NORMAL"; return "LOW"; }
 public static BigDecimal safeAmount(BigDecimal amount) { return amount==null?BigDecimal.ZERO:amount.max(BigDecimal.ZERO); }
 public static BigDecimal percentage(BigDecimal numerator,BigDecimal denominator) { if(denominator==null||denominator.signum()==0)return BigDecimal.ZERO; return safeAmount(numerator).multiply(new BigDecimal("100")).divide(denominator,2,java.math.RoundingMode.HALF_UP); }
 public static List<String> validateRequired(String title,String owner,String reference) { List<String> errors=new ArrayList<>(); if(title==null||title.isBlank())errors.add("Title is required"); if(owner==null||owner.isBlank())errors.add("Owner is required"); if(reference!=null&&reference.length()>100)errors.add("Reference is too long"); return errors; }
 public static Map<String,Object> classify(BigDecimal amount,LocalDate dueDate,String status,boolean evidence) { Map<String,Object> result=new LinkedHashMap<>(); result.put("amount",safeAmount(amount)); result.put("dueInDays",daysUntil(dueDate)); result.put("urgency",urgency(dueDate)); result.put("terminal",isTerminal(status)); result.put("evidencePresent",evidence); result.put("statusKnown",isKnownStatus(status)); return result; }
    /** Policy check: validateForecast for revenue planning. */
    public static boolean validateForecast(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("5000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large || !evidencePresent;
    }
    /** Policy check: variancePercent for revenue planning. */
    public static boolean variancePercent(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("10000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return due && !terminal;
    }
    /** Policy check: isForecastOverBudget for revenue planning. */
    public static boolean isForecastOverBudget(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("25000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large && !approved;
    }
    /** Policy check: requiresExecutiveReview for revenue planning. */
    public static boolean requiresExecutiveReview(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("50000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return evidencePresent && !terminal;
    }
    /** Policy check: forecastConfidence for revenue planning. */
    public static boolean forecastConfidence(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("100000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && (due || large);
    }
    /** Policy check: comparePeriods for revenue planning. */
    public static boolean comparePeriods(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("5000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return approved && evidencePresent;
    }
    /** Policy check: classifyVariance for revenue planning. */
    public static boolean classifyVariance(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("10000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && evidencePresent;
    }
    /** Policy check: validateAssumptions for revenue planning. */
    public static boolean validateAssumptions(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("25000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large || due;
    }
    /** Policy check: reviewReadiness for revenue planning. */
    public static boolean reviewReadiness(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("50000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && approved;
    }
    /** Policy check: closeReadiness for revenue planning. */
    public static boolean closeReadiness(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("100000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return due && !evidencePresent;
    }

}
