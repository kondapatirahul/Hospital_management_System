package com.healthcare.hospital.enterprise.workflows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WorkflowAnalytics {
    private WorkflowAnalytics() {}
    public static String text(String value) { return value == null ? "" : value.trim(); }
    public static String key(String value) { return text(value).toUpperCase(Locale.ROOT).replace(' ', '_'); }
    public static boolean contains(String value, String query) {
        return query == null || query.isBlank() || (value != null && value.toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT)));
    }
    public static <T> Map<String, Long> count(Collection<T> values, Function<T,String> classifier) {
        if (values == null) return Map.of();
        Map<String, Long> result = new LinkedHashMap<>();
        values.stream().filter(Objects::nonNull).forEach(v -> result.merge(key(classifier.apply(v)), 1L, Long::sum));
        return Map.copyOf(result);
    }
    public static <T> List<T> safe(Collection<T> values) {
        return values == null ? List.of() : values.stream().filter(Objects::nonNull).toList();
    }
    public static boolean between(LocalDate date, LocalDate from, LocalDate to) {
        return date != null && (from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to));
    }
    public static boolean overdue(LocalDateTime due, LocalDateTime now, String status) {
        return due != null && now != null && due.isBefore(now) && !List.of("DONE","COMPLETED","CANCELLED","CLOSED").contains(key(status));
    }
    public static WorkflowSnapshot snapshot(String domain, Collection<String> statuses, long total, long active, long attention, List<String> warnings) {
        Map<String,Long> counts = statuses == null ? Map.of() :
            statuses.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(WorkflowAnalytics::key, LinkedHashMap::new, Collectors.counting()));
        return new WorkflowSnapshot(domain,total,active,attention,counts,warnings == null ? List.of() : warnings);
    }
}
