package com.healthcare.hospital.enterprise.policy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Business rules for IncidentReport. Kept free of persistence and web concerns so
 * rules can be reused by MVC controllers, REST endpoints, scheduled jobs, and tests.
 */
public final class IncidentReportPolicy {
    public static final String WORKFLOW = "IncidentReport";
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 200;
    public static final int MAX_TEXT_LENGTH = 2000;
    private static final Pattern SAFE_REFERENCE = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._/-]{1,79}");
    private static final Set<String> TERMINAL = Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED");
    private static final Set<String> ACTIVE = Set.of("NEW", "OPEN", "PENDING", "IN_PROGRESS", "UNDER_REVIEW", "ASSIGNED");
    private static final Map<String, Set<String>> TRANSITIONS = transitions();

    private IncidentReportPolicy() { throw new IllegalStateException("Utility class"); }

    private static Map<String, Set<String>> transitions() {
        Map<String, Set<String>> rules = new LinkedHashMap<>();
        rules.put("NEW", Set.of("OPEN", "PENDING", "ASSIGNED", "CANCELLED"));
        rules.put("OPEN", Set.of("PENDING", "IN_PROGRESS", "ASSIGNED", "CANCELLED"));
        rules.put("PENDING", Set.of("IN_PROGRESS", "UNDER_REVIEW", "APPROVED", "REJECTED", "CANCELLED"));
        rules.put("ASSIGNED", Set.of("IN_PROGRESS", "PENDING", "CANCELLED"));
        rules.put("IN_PROGRESS", Set.of("UNDER_REVIEW", "COMPLETED", "ON_HOLD", "CANCELLED"));
        rules.put("ON_HOLD", Set.of("IN_PROGRESS", "CANCELLED"));
        rules.put("UNDER_REVIEW", Set.of("APPROVED", "REJECTED", "IN_PROGRESS", "COMPLETED"));
        rules.put("APPROVED", Set.of("IN_PROGRESS", "COMPLETED", "ARCHIVED"));
        rules.put("COMPLETED", Set.of("ARCHIVED"));
        rules.put("REJECTED", Set.of("PENDING", "ARCHIVED"));
        rules.put("CANCELLED", Set.of("ARCHIVED"));
        rules.put("ARCHIVED", Set.of());
        return Map.copyOf(rules);
    }

    public static String normalizeStatus(String value) {
        if (value == null || value.isBlank()) return "NEW";
        return value.trim().replace(' ', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    }

    public static boolean isTerminal(String status) { return TERMINAL.contains(normalizeStatus(status)); }
    public static boolean isActive(String status) { return ACTIVE.contains(normalizeStatus(status)); }
    public static boolean canTransition(String from, String to) {
        String source = normalizeStatus(from); String target = normalizeStatus(to);
        return Objects.equals(source, target) || TRANSITIONS.getOrDefault(source, Set.of()).contains(target);
    }

    public static String requireTransition(String from, String to) {
        String source = normalizeStatus(from); String target = normalizeStatus(to);
        if (!canTransition(source, target)) throw new IllegalArgumentException("Invalid IncidentReport transition: " + source + " -> " + target);
        return target;
    }

    public static String cleanText(String value, String field, int maxLength) {
        if (value == null) return "";
        String clean = value.trim();
        if (clean.length() > maxLength) throw new IllegalArgumentException(field + " exceeds " + maxLength + " characters");
        if (clean.indexOf('\0') >= 0) throw new IllegalArgumentException(field + " contains an invalid character");
        return clean;
    }

    public static String requireText(String value, String field, int maxLength) {
        String clean = cleanText(value, field, maxLength);
        if (clean.isBlank()) throw new IllegalArgumentException(field + " is required");
        return clean;
    }

    public static String validateReference(String value) {
        String reference = requireText(value, "referenceCode", 80);
        if (!SAFE_REFERENCE.matcher(reference).matches()) throw new IllegalArgumentException("referenceCode contains unsupported characters");
        return reference;
    }

    public static int pageSize(int requested) {
        if (requested < 1) return DEFAULT_PAGE_SIZE;
        return Math.min(requested, MAX_PAGE_SIZE);
    }

    public static int pageIndex(int requested) { return Math.max(0, requested); }

    public static BigDecimal nonNegativeAmount(BigDecimal amount, String field) {
        BigDecimal value = amount == null ? BigDecimal.ZERO : amount;
        if (value.signum() < 0) throw new IllegalArgumentException(field + " cannot be negative");
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public static boolean isOverdue(LocalDate dueDate, String status, LocalDate today) {
        return dueDate != null && today != null && dueDate.isBefore(today) && !isTerminal(status);
    }

    public static long ageInHours(Instant createdAt, Instant now) {
        if (createdAt == null || now == null || now.isBefore(createdAt)) return 0L;
        return Duration.between(createdAt, now).toHours();
    }

    public static boolean matchesSearch(Collection<String> fields, String query) {
        if (query == null || query.isBlank()) return true;
        String needle = query.trim().toLowerCase(Locale.ROOT);
        if (fields == null) return false;
        return fields.stream().filter(Objects::nonNull).map(s -> s.toLowerCase(Locale.ROOT)).anyMatch(s -> s.contains(needle));
    }

    public static List<String> distinctSorted(Collection<String> values) {
        if (values == null) return List.of();
        TreeSet<String> sorted = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String value : values) if (value != null && !value.isBlank()) sorted.add(value.trim());
        return List.copyOf(sorted);
    }

    public static <T> List<T> safeSort(Collection<T> values, Comparator<? super T> comparator) {
        if (values == null || values.isEmpty()) return List.of();
        List<T> copy = new ArrayList<>(values);
        if (comparator != null) copy.sort(comparator);
        return List.copyOf(copy);
    }

    public static Map<String, Long> statusCounts(Collection<String> statuses) {
        Map<String, Long> counts = new LinkedHashMap<>();
        if (statuses == null) return counts;
        for (String status : statuses) counts.merge(normalizeStatus(status), 1L, Long::sum);
        return counts;
    }

    public static Optional<LocalDate> latestDate(Collection<LocalDate> dates) {
        if (dates == null) return Optional.empty();
        return dates.stream().filter(Objects::nonNull).max(Comparator.naturalOrder());
    }

    public static String safeActor(String actor) {
        String value = cleanText(actor, "actor", 120);
        return value.isBlank() ? "system" : value;
    }

    public static void validateDueDate(LocalDate dueDate, LocalDate createdDate) {
        if (dueDate != null && createdDate != null && dueDate.isBefore(createdDate))
            throw new IllegalArgumentException("dueDate cannot be before createdDate");
    }

    public static void validateStatus(String status) {
        String normalized = normalizeStatus(status);
        if (!TRANSITIONS.containsKey(normalized)) throw new IllegalArgumentException("Unknown workflow status: " + normalized);
    }

    public static boolean shouldNotify(String oldStatus, String newStatus) {
        return !normalizeStatus(oldStatus).equals(normalizeStatus(newStatus));
    }

    public static String summaryLabel(String status) {
        String normalized = normalizeStatus(status);
        return switch (normalized) {
            case "NEW" -> "New"; case "OPEN" -> "Open"; case "PENDING" -> "Pending action";
            case "IN_PROGRESS" -> "In progress"; case "UNDER_REVIEW" -> "Under review";
            case "APPROVED" -> "Approved"; case "REJECTED" -> "Rejected"; case "ON_HOLD" -> "On hold";
            case "COMPLETED" -> "Completed"; case "CANCELLED" -> "Cancelled"; case "ARCHIVED" -> "Archived";
            case "ASSIGNED" -> "Assigned"; default -> normalized;
        };
    }

    public static Map<String, Object> workflowSummary(Collection<String> statuses, Collection<LocalDate> dueDates, LocalDate today) {
        List<String> statusList = statuses == null ? List.of() : List.copyOf(statuses);
        long activeCount = statusList.stream().filter(IncidentReportPolicy::isActive).count();
        long terminalCount = statusList.stream().filter(IncidentReportPolicy::isTerminal).count();
        long overdueCount = dueDates == null ? 0 : dueDates.stream().filter(d -> isOverdue(d, "IN_PROGRESS", today)).count();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("workflow", WORKFLOW); summary.put("total", statusList.size());
        summary.put("active", activeCount); summary.put("terminal", terminalCount);
        summary.put("overdue", overdueCount); summary.put("statuses", statusCounts(statusList));
        return Map.copyOf(summary);
    }

    public static String redactForLog(String value) {
        if (value == null || value.isBlank()) return "";
        String clean = value.trim();
        if (clean.length() <= 4) return "****";
        return clean.substring(0, 2) + "***" + clean.substring(clean.length() - 2);
    }

    public static boolean isValidDateRange(LocalDate from, LocalDate to) {
        return from == null || to == null || !to.isBefore(from);
    }

    public static BigDecimal calculateTax(BigDecimal net, BigDecimal ratePercent) {
        BigDecimal base = nonNegativeAmount(net, "netAmount");
        BigDecimal rate = nonNegativeAmount(ratePercent, "taxRate");
        if (rate.compareTo(new BigDecimal("100.00")) > 0) throw new IllegalArgumentException("taxRate cannot exceed 100%");
        return base.multiply(rate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTotal(BigDecimal net, BigDecimal tax, BigDecimal discount) {
        BigDecimal base = nonNegativeAmount(net, "netAmount");
        BigDecimal taxValue = nonNegativeAmount(tax, "taxAmount");
        BigDecimal discountValue = nonNegativeAmount(discount, "discountAmount");
        if (discountValue.compareTo(base.add(taxValue)) > 0) throw new IllegalArgumentException("discount exceeds gross amount");
        return base.add(taxValue).subtract(discountValue).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public static List<String> validationMessages(String reference, String title, String status) {
        List<String> errors = new ArrayList<>();
        try { validateReference(reference); } catch (IllegalArgumentException ex) { errors.add(ex.getMessage()); }
        try { requireText(title, "title", 180); } catch (IllegalArgumentException ex) { errors.add(ex.getMessage()); }
        try { validateStatus(status); } catch (IllegalArgumentException ex) { errors.add(ex.getMessage()); }
        return List.copyOf(errors);
    }

    public static boolean canEdit(String status, boolean privileged) {
        if (privileged) return !"ARCHIVED".equals(normalizeStatus(status));
        return !isTerminal(status) && !"UNDER_REVIEW".equals(normalizeStatus(status));
    }

    public static String normalizeSearchTerm(String query) {
        if (query == null) return "";
        return query.trim().replaceAll("\s+", " ").toLowerCase(Locale.ROOT);
    }

    public static int comparePriority(String left, String right) {
        List<String> order = List.of("CRITICAL", "URGENT", "HIGH", "NORMAL", "LOW");
        int li = order.indexOf(left == null ? "NORMAL" : left.toUpperCase(Locale.ROOT));
        int ri = order.indexOf(right == null ? "NORMAL" : right.toUpperCase(Locale.ROOT));
        if (li < 0) li = order.indexOf("NORMAL"); if (ri < 0) ri = order.indexOf("NORMAL");
        return Integer.compare(li, ri);
    }

    public static String normalizePriority(String value) {
        String priority = value == null ? "NORMAL" : value.trim().toUpperCase(Locale.ROOT);
        return Set.of("LOW", "NORMAL", "HIGH", "URGENT", "CRITICAL").contains(priority) ? priority : "NORMAL";
    }

    public static boolean isDueSoon(LocalDate dueDate, LocalDate today, int horizonDays) {
        if (dueDate == null || today == null || horizonDays < 0) return false;
        return !dueDate.isBefore(today) && !dueDate.isAfter(today.plusDays(horizonDays));
    }

    public static String requireOneOf(String value, String field, Set<String> allowed) {
        String normalized = requireText(value, field, 80).toUpperCase(Locale.ROOT);
        if (allowed == null || !allowed.contains(normalized)) throw new IllegalArgumentException("Unsupported " + field + ": " + normalized);
        return normalized;
    }

    public static Map<String, Object> auditSnapshot(String actor, String status, Instant at) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("workflow", WORKFLOW); snapshot.put("actor", safeActor(actor));
        snapshot.put("status", normalizeStatus(status)); snapshot.put("timestamp", at == null ? Instant.now().toString() : at.toString());
        return Map.copyOf(snapshot);
    }

    public static boolean isReferenceDuplicate(Collection<String> references, String candidate) {
        if (references == null || candidate == null) return false;
        String normalized = candidate.trim().toLowerCase(Locale.ROOT);
        return references.stream().filter(Objects::nonNull).map(v -> v.trim().toLowerCase(Locale.ROOT)).anyMatch(normalized::equals);
    }

    public static String truncate(String value, int limit) {
        if (value == null || limit < 1) return "";
        String clean = value.trim(); return clean.length() <= limit ? clean : clean.substring(0, limit - 1) + "…";
    }

    public static long countMatching(Collection<String> values, String query) {
        if (values == null) return 0;
        return values.stream().filter(v -> matchesSearch(List.of(v == null ? "" : v), query)).count();
    }

    public static boolean requiresApproval(BigDecimal amount, BigDecimal threshold) {
        if (amount == null || threshold == null) return false;
        return amount.compareTo(threshold) >= 0;
    }

    public static String workflowKey(String reference, Long id) {
        String ref = reference == null || reference.isBlank() ? "UNASSIGNED" : reference.trim();
        return WORKFLOW.toUpperCase(Locale.ROOT) + ":" + ref + (id == null ? "" : ":" + id);
    }

    public static Set<String> terminalStatuses() { return TERMINAL; }
    public static Set<String> activeStatuses() { return ACTIVE; }
    public static Map<String, Set<String>> allowedTransitions() { return TRANSITIONS; }
}
