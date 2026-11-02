package com.healthcare.hospital.modules.operations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Production operations engine for Appointment.
 * This class is deliberately persistence-agnostic: the application service layer can
 * persist returned immutable records through the existing repositories.
 */
public final class AppointmentCapacityService {
    public static final String DOMAIN = "APPOINTMENT";
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 500;
    public static final int MAX_NOTE_LENGTH = 2000;
    private static final Set<String> STATES = Set.of("REQUESTED", "CONFIRMED", "CHECKED_IN", "IN_CONSULTATION", "COMPLETED", "CANCELLED", "NO_SHOW");
    private static final Set<String> TERMINAL = Set.of("COMPLETED", "CANCELLED", "NO_SHOW");
    private static final Map<String, Set<String>> TRANSITIONS = transitionGraph();

    public record Record(
            String id, String reference, String slotId, String doctorId, String patientId,
            String status, String priority, Instant createdAt, Instant updatedAt,
            Instant dueAt, String assignedTo, String description, long version) {
        public Record {
            id = OperationSupport.required(id, "id", 64);
            reference = OperationSupport.reference(reference, "reference");
            slotId = OperationSupport.required(slotId, "slotId", 80);
            doctorId = OperationSupport.required(doctorId, "doctorId", 100);
            patientId = OperationSupport.required(patientId, "patientId", 100);
            status = normalizeStatus(status);
            priority = normalizePriority(priority);
            createdAt = Objects.requireNonNull(createdAt, "createdAt");
            updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
            if (updatedAt.isBefore(createdAt)) throw new IllegalArgumentException("updatedAt cannot precede createdAt");
            if (dueAt != null && dueAt.isBefore(createdAt)) throw new IllegalArgumentException("dueAt cannot precede createdAt");
            assignedTo = OperationSupport.optional(assignedTo, "assignedTo", 100);
            description = OperationSupport.optional(description, "description", MAX_NOTE_LENGTH);
            if (version < 0) throw new IllegalArgumentException("version cannot be negative");
        }
    }

    public record Event(String id, String recordId, String actor, String action,
                        String fromStatus, String toStatus, Instant occurredAt,
                        String reason, long sequence) {
        public Event {
            id = OperationSupport.required(id, "eventId", 64);
            recordId = OperationSupport.required(recordId, "recordId", 64);
            actor = OperationSupport.required(actor, "actor", 100);
            action = OperationSupport.required(action, "action", 60);
            fromStatus = OperationSupport.optional(fromStatus, "fromStatus", 40);
            toStatus = OperationSupport.optional(toStatus, "toStatus", 40);
            occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
            reason = OperationSupport.optional(reason, "reason", MAX_NOTE_LENGTH);
            if (sequence < 1) throw new IllegalArgumentException("sequence must be positive");
        }
    }

    public record Summary(long total, long active, long terminal, long overdue,
                          Map<String, Long> byStatus, Map<String, Long> byPriority) {
        public Summary {
            if (total < 0 || active < 0 || terminal < 0 || overdue < 0) throw new IllegalArgumentException("Counts cannot be negative");
            byStatus = Map.copyOf(byStatus);
            byPriority = Map.copyOf(byPriority);
        }
    }

    public record Query(String text, Set<String> statuses, Set<String> priorities,
                        String assignedTo, Instant dueBefore, Instant dueAfter,
                        int page, int size, String sortBy, boolean descending) {
        public Query {
            text = OperationSupport.optional(text, "query", 160);
            statuses = statuses == null ? Set.of() : statuses.stream().map(AppointmentCapacityService::normalizeStatus).collect(Collectors.toUnmodifiableSet());
            priorities = priorities == null ? Set.of() : priorities.stream().map(AppointmentCapacityService::normalizePriority).collect(Collectors.toUnmodifiableSet());
            assignedTo = OperationSupport.optional(assignedTo, "assignedTo", 100);
            if (dueBefore != null && dueAfter != null && dueBefore.isBefore(dueAfter)) throw new IllegalArgumentException("dueBefore cannot precede dueAfter");
            page = Math.max(0, page);
            size = size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
            sortBy = normalizeSort(sortBy);
        }
    }

    public record Decision(boolean allowed, String code, String message) {
        public Decision {
            code = OperationSupport.required(code, "decisionCode", 60);
            message = OperationSupport.optional(message, "message", 500);
        }
        public static Decision allow(String message) { return new Decision(true, "ALLOWED", message); }
        public static Decision deny(String code, String message) { return new Decision(false, code, message); }
    }

    public record Metric(String name, BigDecimal value, String unit, Instant measuredAt) {
        public Metric {
            name = OperationSupport.required(name, "metricName", 80);
            value = Objects.requireNonNull(value, "value").setScale(3, RoundingMode.HALF_UP);
            unit = OperationSupport.required(unit, "unit", 30);
            measuredAt = Objects.requireNonNull(measuredAt, "measuredAt");
        }
    }

    private AppointmentCapacityService() { throw new IllegalStateException("Utility class"); }

    private static Map<String, Set<String>> transitionGraph() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("REQUESTED", Set.of("CONFIRMED", "CHECKED_IN", "CANCELLED"));
        graph.put("CONFIRMED", Set.of("CHECKED_IN", "IN_CONSULTATION", "CANCELLED"));
        graph.put("CHECKED_IN", Set.of("IN_CONSULTATION", "COMPLETED", "CANCELLED"));
        graph.put("IN_CONSULTATION", Set.of("COMPLETED", "CANCELLED"));
        graph.put("COMPLETED", Set.of());
        graph.put("CANCELLED", Set.of());
        graph.put("NO_SHOW", Set.of());
        return Map.copyOf(graph);
    }

    public static String normalizeStatus(String value) {
        return OperationSupport.status(value, "CONFIRMED");
    }

    public static String normalizePriority(String value) {
        if (value == null || value.isBlank()) return "NORMAL";
        String priority = value.trim().toUpperCase(Locale.ROOT);
        return switch (priority) {
            case "CRITICAL", "URGENT", "HIGH", "NORMAL", "LOW" -> priority;
            default -> "NORMAL";
        };
    }

    public static int comparePriority(String left, String right) {
        List<String> order = List.of("CRITICAL", "URGENT", "HIGH", "NORMAL", "LOW");
        return Integer.compare(order.indexOf(normalizePriority(left)), order.indexOf(normalizePriority(right)));
    }

    public static boolean isTerminal(String status) {
        return TERMINAL.contains(normalizeStatus(status));
    }

    public static boolean isActive(String status) {
        return STATES.contains(normalizeStatus(status)) && !isTerminal(status);
    }

    public static boolean canTransition(String from, String to) {
        String source = normalizeStatus(from);
        String target = normalizeStatus(to);
        return source.equals(target) || TRANSITIONS.getOrDefault(source, Set.of()).contains(target);
    }

    public static String requireTransition(String from, String to) {
        String source = normalizeStatus(from);
        String target = normalizeStatus(to);
        if (!STATES.contains(source) || !STATES.contains(target)) throw new IllegalArgumentException("Unknown state");
        if (!canTransition(source, target)) throw new IllegalStateException("Invalid transition " + source + " -> " + target);
        return target;
    }

    public static Record create(String reference, String itemId, String owner, String context,
                                String priority, Instant dueAt, String description, Clock clock) {
        Instant now = OperationSupport.now(clock);
        return new Record(OperationSupport.id(), reference, itemId, owner, context, "CONFIRMED",
                priority, now, now, dueAt, "", description, 0);
    }

    public static Record assign(Record record, String assignee, String actor, Clock clock) {
        Objects.requireNonNull(record, "record");
        String target = OperationSupport.required(assignee, "assignee", 100);
        if (isTerminal(record.status())) throw new IllegalStateException("Terminal records cannot be assigned");
        Instant now = OperationSupport.now(clock);
        return copy(record, record.status(), target, record.description(), now);
    }

    public static Record transition(Record record, String target, String actor, String reason, Clock clock) {
        Objects.requireNonNull(record, "record");
        String next = requireTransition(record.status(), target);
        OperationSupport.required(actor, "actor", 100);
        String note = OperationSupport.optional(reason, "reason", MAX_NOTE_LENGTH);
        if (next.equals(record.status())) return record;
        Instant now = OperationSupport.now(clock);
        return copy(record, next, record.assignedTo(), note.isBlank() ? record.description() : note, now);
    }

    public static Record updateDescription(Record record, String description, String actor, Clock clock) {
        Objects.requireNonNull(record, "record");
        OperationSupport.required(actor, "actor", 100);
        String clean = OperationSupport.optional(description, "description", MAX_NOTE_LENGTH);
        return copy(record, record.status(), record.assignedTo(), clean, OperationSupport.now(clock));
    }

    private static Record copy(Record old, String status, String assignee, String description, Instant now) {
        return new Record(old.id(), old.reference(), old.slotId(), old.doctorId(), old.patientId(),
                status, old.priority(), old.createdAt(), now, old.dueAt(), assignee, description, old.version() + 1);
    }

    public static Event event(Record before, Record after, String actor, String action, String reason,
                              long sequence, Clock clock) {
        Objects.requireNonNull(after, "after");
        String from = before == null ? "" : before.status();
        String to = after.status();
        return new Event(OperationSupport.id(), after.id(), actor, action, from, to,
                OperationSupport.now(clock), reason, sequence);
    }

    public static Decision authorizeMutation(Record record, String actor, boolean privileged) {
        if (record == null) return Decision.deny("NOT_FOUND", "Record does not exist");
        if (actor == null || actor.isBlank()) return Decision.deny("ACTOR_REQUIRED", "An authenticated actor is required");
        if (isTerminal(record.status()) && !privileged) return Decision.deny("TERMINAL_LOCKED", "Terminal records require privileged correction");
        if (!privileged && !record.assignedTo().isBlank() && !record.assignedTo().equalsIgnoreCase(actor)) {
            return Decision.deny("NOT_ASSIGNED", "Record is assigned to another operator");
        }
        return Decision.allow("Mutation is permitted");
    }

    public static boolean isOverdue(Record record, Instant now) {
        return record != null && record.dueAt() != null && now != null
                && record.dueAt().isBefore(now) && !isTerminal(record.status());
    }

    public static long ageMinutes(Record record, Instant now) {
        if (record == null || now == null || now.isBefore(record.createdAt())) return 0;
        return Duration.between(record.createdAt(), now).toMinutes();
    }

    public static long remainingMinutes(Record record, Instant now) {
        if (record == null || record.dueAt() == null || now == null || !record.dueAt().isAfter(now)) return 0;
        return Duration.between(now, record.dueAt()).toMinutes();
    }

    public static boolean dueWithin(Record record, Instant now, long minutes) {
        if (record == null || record.dueAt() == null || now == null || minutes < 0) return false;
        return !record.dueAt().isBefore(now) && !record.dueAt().isAfter(now.plusSeconds(minutes * 60));
    }

    public static String slaBand(Record record, Instant now) {
        if (record == null || isTerminal(record.status())) return "CLOSED";
        if (isOverdue(record, now)) return "BREACHED";
        if (dueWithin(record, now, 60)) return "DUE_SOON";
        if (dueWithin(record, now, 240)) return "WATCH";
        return "ON_TRACK";
    }

    public static List<String> validate(Record record) {
        List<String> errors = new ArrayList<>();
        if (record == null) return List.of("record is required");
        try { OperationSupport.reference(record.reference(), "reference"); } catch (RuntimeException ex) { errors.add(ex.getMessage()); }
        if (!STATES.contains(record.status())) errors.add("status is not supported by this workflow");
        if (record.version() < 0) errors.add("version cannot be negative");
        if (record.updatedAt().isBefore(record.createdAt())) errors.add("updatedAt precedes createdAt");
        if (record.dueAt() != null && record.dueAt().isBefore(record.createdAt())) errors.add("dueAt precedes createdAt");
        if (record.description().length() > MAX_NOTE_LENGTH) errors.add("description is too long");
        return List.copyOf(errors);
    }

    public static List<Record> filter(Collection<Record> records, Query query) {
        if (records == null || records.isEmpty()) return List.of();
        Query q = query == null ? new Query("", Set.of(), Set.of(), "", null, null, 0, MAX_PAGE_SIZE, "updatedAt", true) : query;
        String needle = OperationSupport.search(q.text());
        return records.stream().filter(Objects::nonNull)
                .filter(r -> q.statuses().isEmpty() || q.statuses().contains(r.status()))
                .filter(r -> q.priorities().isEmpty() || q.priorities().contains(r.priority()))
                .filter(r -> q.assignedTo().isBlank() || r.assignedTo().equalsIgnoreCase(q.assignedTo()))
                .filter(r -> q.dueBefore() == null || (r.dueAt() != null && !r.dueAt().isAfter(q.dueBefore())))
                .filter(r -> q.dueAfter() == null || (r.dueAt() != null && !r.dueAt().isBefore(q.dueAfter())))
                .filter(r -> needle.isEmpty() || searchable(r).contains(needle))
                .sorted(comparator(q.sortBy(), q.descending())).toList();
    }

    private static String searchable(Record r) {
        return String.join(" ", r.id(), r.reference(), r.slotId(), r.doctorId(), r.patientId(),
                r.status(), r.priority(), r.assignedTo(), r.description()).toLowerCase(Locale.ROOT);
    }

    private static Comparator<Record> comparator(String sortBy, boolean descending) {
        Comparator<Record> c = switch (sortBy) {
            case "reference" -> Comparator.comparing(Record::reference, String.CASE_INSENSITIVE_ORDER);
            case "status" -> Comparator.comparing(Record::status);
            case "priority" -> (a,b) -> comparePriority(a.priority(), b.priority());
            case "createdAt" -> Comparator.comparing(Record::createdAt);
            case "dueAt" -> Comparator.comparing(Record::dueAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "assignedTo" -> Comparator.comparing(Record::assignedTo, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Record::updatedAt);
        };
        return descending ? c.reversed() : c;
    }

    private static String normalizeSort(String value) {
        if (value == null) return "updatedAt";
        return switch (value) {
            case "reference", "status", "priority", "createdAt", "dueAt", "assignedTo", "updatedAt" -> value;
            default -> "updatedAt";
        };
    }

    public static OperationPage<Record> page(List<Record> records, int page, int size) {
        List<Record> values = records == null ? List.of() : records;
        int p = OperationSupport.page(page);
        int s = OperationSupport.pageSize(size);
        long startLong = (long) p * s;
        int start = (int) Math.min(values.size(), startLong);
        int end = Math.min(values.size(), start + s);
        return new OperationPage<>(p, s, values.size(), values.subList(start, end));
    }

    public static Summary summarize(Collection<Record> records, Instant now) {
        List<Record> values = records == null ? List.of() : records.stream().filter(Objects::nonNull).toList();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        Map<String, Long> byPriority = new LinkedHashMap<>();
        long active = 0, terminal = 0, overdue = 0;
        for (Record record : values) {
            byStatus.merge(record.status(), 1L, Long::sum);
            byPriority.merge(record.priority(), 1L, Long::sum);
            if (isTerminal(record.status())) terminal++; else active++;
            if (isOverdue(record, now)) overdue++;
        }
        return new Summary(values.size(), active, terminal, overdue, byStatus, byPriority);
    }

    public static Optional<Record> findByReference(Collection<Record> records, String reference) {
        if (records == null || reference == null) return Optional.empty();
        String key = reference.trim().toLowerCase(Locale.ROOT);
        return records.stream().filter(Objects::nonNull)
                .filter(r -> r.reference().toLowerCase(Locale.ROOT).equals(key)).findFirst();
    }

    public static boolean hasDuplicateReference(Collection<Record> records, String reference, String excludingId) {
        if (records == null || reference == null) return false;
        String key = reference.trim().toLowerCase(Locale.ROOT);
        return records.stream().filter(Objects::nonNull)
                .filter(r -> excludingId == null || !r.id().equals(excludingId))
                .anyMatch(r -> r.reference().toLowerCase(Locale.ROOT).equals(key));
    }

    public static List<Event> validateEventSequence(Collection<Event> events, String recordId) {
        if (events == null) return List.of();
        List<Event> selected = events.stream().filter(Objects::nonNull)
                .filter(e -> e.recordId().equals(recordId)).sorted(Comparator.comparingLong(Event::sequence)).toList();
        long expected = 1;
        for (Event event : selected) {
            if (event.sequence() != expected++) throw new IllegalStateException("Event sequence contains a gap");
        }
        return selected;
    }

    public static Map<String, Long> countByAssignee(Collection<Record> records) {
        Map<String, Long> counts = new LinkedHashMap<>();
        if (records == null) return counts;
        records.stream().filter(Objects::nonNull).filter(r -> !r.assignedTo().isBlank())
                .sorted(Comparator.comparing(Record::assignedTo, String.CASE_INSENSITIVE_ORDER))
                .forEach(r -> counts.merge(r.assignedTo(), 1L, Long::sum));
        return Map.copyOf(counts);
    }

    public static List<Record> overdue(Collection<Record> records, Instant now) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> isOverdue(r, now))
                .sorted(Comparator.comparing(Record::dueAt)).toList();
    }

    public static List<Record> dueSoon(Collection<Record> records, Instant now, long minutes) {
        if (records == null || now == null || minutes < 0) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> dueWithin(r, now, minutes))
                .sorted(Comparator.comparing(Record::dueAt)).toList();
    }

    public static BigDecimal completionRate(Collection<Record> records) {
        if (records == null || records.isEmpty()) return BigDecimal.ZERO.setScale(2);
        long complete = records.stream().filter(Objects::nonNull).filter(r -> TERMINAL.contains(r.status())).count();
        return BigDecimal.valueOf(complete).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
    }

    public static Map<String, Object> dashboard(Collection<Record> records, Instant now) {
        Summary summary = summarize(records, now);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("domain", DOMAIN);
        result.put("total", summary.total());
        result.put("active", summary.active());
        result.put("terminal", summary.terminal());
        result.put("overdue", summary.overdue());
        result.put("byStatus", summary.byStatus());
        result.put("byPriority", summary.byPriority());
        result.put("completionRate", completionRate(records));
        result.put("generatedAt", now == null ? Instant.EPOCH : now);
        return Map.copyOf(result);
    }

    public static Metric averageAgeMinutes(Collection<Record> records, Instant now) {
        List<Record> values = records == null ? List.of() : records.stream().filter(Objects::nonNull).toList();
        BigDecimal average = values.isEmpty() ? BigDecimal.ZERO
                : values.stream().map(r -> BigDecimal.valueOf(ageMinutes(r, now)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(values.size()), 3, RoundingMode.HALF_UP);
        return new Metric("averageAge", average, "minutes", now == null ? Instant.EPOCH : now);
    }

    public static Metric overdueRate(Collection<Record> records, Instant now) {
        List<Record> values = records == null ? List.of() : records.stream().filter(Objects::nonNull).toList();
        BigDecimal rate = values.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(values.stream().filter(r -> isOverdue(r, now)).count())
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(values.size()), 3, RoundingMode.HALF_UP);
        return new Metric("overdueRate", rate, "percent", now == null ? Instant.EPOCH : now);
    }

    public static List<Record> requireValidBatch(Collection<Record> records) {
        if (records == null) return List.of();
        List<String> errors = new ArrayList<>();
        List<Record> copy = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        Set<String> refs = new HashSet<>();
        for (Record record : records) {
            if (record == null) { errors.add("batch contains a null record"); continue; }
            errors.addAll(validate(record));
            if (!ids.add(record.id())) errors.add("duplicate id: " + record.id());
            if (!refs.add(record.reference().toLowerCase(Locale.ROOT))) errors.add("duplicate reference: " + record.reference());
            copy.add(record);
        }
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join("; ", errors));
        return List.copyOf(copy);
    }

    public static List<Record> applyPredicate(Collection<Record> records, Predicate<Record> predicate) {
        if (records == null || predicate == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(predicate).toList();
    }

    public static Map<String, List<Record>> groupByStatus(Collection<Record> records) {
        Map<String, List<Record>> groups = new LinkedHashMap<>();
        if (records == null) return groups;
        records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::createdAt))
                .forEach(r -> groups.computeIfAbsent(r.status(), ignored -> new ArrayList<>()).add(r));
        Map<String, List<Record>> immutable = new LinkedHashMap<>();
        groups.forEach((key,value) -> immutable.put(key, List.copyOf(value)));
        return Map.copyOf(immutable);
    }

    public static Map<String, List<Record>> groupByOwner(Collection<Record> records) {
        Map<String, List<Record>> groups = new LinkedHashMap<>();
        if (records == null) return groups;
        records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::reference))
                .forEach(r -> groups.computeIfAbsent(r.doctorId(), ignored -> new ArrayList<>()).add(r));
        Map<String, List<Record>> immutable = new LinkedHashMap<>();
        groups.forEach((key,value) -> immutable.put(key, List.copyOf(value)));
        return Map.copyOf(immutable);
    }

    public static List<Record> stale(Collection<Record> records, Instant now, long thresholdMinutes) {
        if (records == null || now == null || thresholdMinutes < 0) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> !isTerminal(r.status()))
                .filter(r -> ageMinutes(r, now) >= thresholdMinutes)
                .sorted(Comparator.comparing(Record::updatedAt)).toList();
    }

    public static Decision checkOptimisticVersion(Record current, long expectedVersion) {
        if (current == null) return Decision.deny("NOT_FOUND", "Record was not found");
        if (current.version() != expectedVersion) return Decision.deny("VERSION_CONFLICT", "Record changed; reload before saving");
        return Decision.allow("Version matches");
    }

    public static Record touch(Record record, String actor, Clock clock) {
        Objects.requireNonNull(record, "record");
        OperationSupport.required(actor, "actor", 100);
        return copy(record, record.status(), record.assignedTo(), record.description(), OperationSupport.now(clock));
    }

    public static List<Record> between(Collection<Record> records, Instant from, Instant to) {
        if (records == null) return List.of();
        if (from != null && to != null && to.isBefore(from)) throw new IllegalArgumentException("to precedes from");
        return records.stream().filter(Objects::nonNull)
                .filter(r -> from == null || !r.createdAt().isBefore(from))
                .filter(r -> to == null || !r.createdAt().isAfter(to))
                .sorted(Comparator.comparing(Record::createdAt)).toList();
    }

    public static long countStatus(Collection<Record> records, String status) {
        if (records == null) return 0;
        String target = normalizeStatus(status);
        return records.stream().filter(Objects::nonNull).filter(r -> r.status().equals(target)).count();
    }

    public static long countPriority(Collection<Record> records, String priority) {
        if (records == null) return 0;
        String target = normalizePriority(priority);
        return records.stream().filter(Objects::nonNull).filter(r -> r.priority().equals(target)).count();
    }

    public static List<String> supportedStates() { return STATES.stream().sorted().toList(); }
    public static Map<String, Set<String>> transitionRules() { return TRANSITIONS; }
    public static Set<String> terminalStates() { return TERMINAL; }

    public static String safeSortField(String field) { return normalizeSort(field); }

    public static String auditKey(Record record) {
        Objects.requireNonNull(record, "record");
        return DOMAIN + ":" + record.reference() + ":" + record.id();
    }

    public static String redact(String value) {
        if (value == null || value.isBlank()) return "";
        String clean = value.trim();
        if (clean.length() <= 4) return "****";
        return clean.substring(0, 2) + "***" + clean.substring(clean.length() - 2);
    }

    public static boolean hasSensitiveDescription(Record record) {
        if (record == null) return false;
        String text = record.description().toLowerCase(Locale.ROOT);
        return text.contains("password") || text.contains("token") || text.contains("secret")
                || text.contains("medical record number") || text.contains("national id");
    }

    public static String exportLine(Record record) {
        Objects.requireNonNull(record, "record");
        return String.join("|", record.reference(), record.slotId(), record.status(),
                record.priority(), record.createdAt().toString(), record.updatedAt().toString(),
                record.dueAt() == null ? "" : record.dueAt().toString(), redact(record.assignedTo()));
    }

    public static List<String> export(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::reference))
                .map(AppointmentCapacityService::exportLine).toList();
    }

    public static Map<String, Long> dailyCreatedCounts(Collection<Record> records, ZoneId zone) {
        Map<String, Long> counts = new LinkedHashMap<>();
        if (records == null || zone == null) return counts;
        records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::createdAt))
                .forEach(r -> counts.merge(r.createdAt().atZone(zone).toLocalDate().toString(), 1L, Long::sum));
        return Map.copyOf(counts);
    }

    public static List<Record> requiringAttention(Collection<Record> records, Instant now) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull)
                .filter(r -> isOverdue(r, now) || "CRITICAL".equals(r.priority()) || "URGENT".equals(r.priority()))
                .sorted(Comparator.comparingInt((Record r) -> comparePriority(r.priority(), "NORMAL"))
                        .thenComparing(Record::updatedAt)).toList();
    }

    public static BigDecimal workloadScore(Record record, Instant now) {
        if (record == null) return BigDecimal.ZERO.setScale(2);
        BigDecimal score = switch (record.priority()) {
            case "CRITICAL" -> new BigDecimal("5.00");
            case "URGENT" -> new BigDecimal("4.00");
            case "HIGH" -> new BigDecimal("3.00");
            case "NORMAL" -> new BigDecimal("2.00");
            default -> new BigDecimal("1.00");
        };
        if (isOverdue(record, now)) score = score.add(new BigDecimal("2.00"));
        if (dueWithin(record, now, 60)) score = score.add(new BigDecimal("1.00"));
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    public static List<Record> workloadOrder(Collection<Record> records, Instant now) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing((Record r) -> workloadScore(r, now)).reversed()
                        .thenComparing(Record::dueAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public static Map<String, BigDecimal> workloadByAssignee(Collection<Record> records, Instant now) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        if (records == null) return result;
        for (Record r : records) {
            if (r == null || r.assignedTo().isBlank()) continue;
            result.merge(r.assignedTo(), workloadScore(r, now), BigDecimal::add);
        }
        return Map.copyOf(result);
    }

    public static boolean canClose(Record record) {
        return record != null && isTerminal(record.status());
    }

    public static boolean canReassign(Record record) {
        return record != null && !isTerminal(record.status());
    }

    public static Record reschedule(Record record, Instant newDueAt, String actor, Clock clock) {
        Objects.requireNonNull(record, "record");
        OperationSupport.required(actor, "actor", 100);
        if (isTerminal(record.status())) throw new IllegalStateException("Terminal record cannot be rescheduled");
        if (newDueAt != null && newDueAt.isBefore(record.createdAt())) throw new IllegalArgumentException("Due time precedes creation");
        return new Record(record.id(), record.reference(), record.slotId(), record.doctorId(),
                record.patientId(), record.status(), record.priority(), record.createdAt(),
                OperationSupport.now(clock), newDueAt, record.assignedTo(), record.description(), record.version() + 1);
    }

    public static Record changePriority(Record record, String priority, String actor, Clock clock) {
        Objects.requireNonNull(record, "record");
        OperationSupport.required(actor, "actor", 100);
        if (isTerminal(record.status())) throw new IllegalStateException("Terminal record cannot change priority");
        return new Record(record.id(), record.reference(), record.slotId(), record.doctorId(),
                record.patientId(), record.status(), normalizePriority(priority), record.createdAt(),
                OperationSupport.now(clock), record.dueAt(), record.assignedTo(), record.description(), record.version() + 1);
    }

    public static List<Record> unassigned(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> r.assignedTo().isBlank())
                .filter(r -> !isTerminal(r.status())).sorted(Comparator.comparing(Record::createdAt)).toList();
    }

    public static List<Record> assignedTo(Collection<Record> records, String assignee) {
        if (records == null || assignee == null || assignee.isBlank()) return List.of();
        return records.stream().filter(Objects::nonNull)
                .filter(r -> r.assignedTo().equalsIgnoreCase(assignee.trim()))
                .sorted(Comparator.comparing(Record::dueAt, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }

    public static Map<String, Object> health(Collection<Record> records, Instant now) {
        Summary s = summarize(records, now);
        String status = s.overdue() > 0 ? "DEGRADED" : (s.active() > 0 ? "OPERATIONAL" : "IDLE");
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("domain", DOMAIN); health.put("status", status);
        health.put("active", s.active()); health.put("overdue", s.overdue());
        health.put("checkedAt", now == null ? Instant.EPOCH : now);
        return Map.copyOf(health);
    }

    public static List<Record> validateAndSort(Collection<Record> records) {
        List<Record> validated = requireValidBatch(records);
        return validated.stream().sorted(Comparator.comparing(Record::createdAt)).toList();
    }

    public static boolean isSameVersion(Record left, Record right) {
        return left != null && right != null && left.id().equals(right.id()) && left.version() == right.version();
    }

    public static Record mergeNonConflicting(Record current, Record incoming, long expectedVersion, Clock clock) {
        Objects.requireNonNull(current, "current");
        Objects.requireNonNull(incoming, "incoming");
        if (!current.id().equals(incoming.id())) throw new IllegalArgumentException("Record ids differ");
        if (current.version() != expectedVersion) throw new IllegalStateException("Version conflict");
        if (!current.reference().equals(incoming.reference())) throw new IllegalArgumentException("Reference cannot be changed");
        if (!current.slotId().equals(incoming.slotId())) throw new IllegalArgumentException("Domain item cannot be changed");
        if (!current.doctorId().equals(incoming.doctorId()) || !current.patientId().equals(incoming.patientId())) {
            throw new IllegalArgumentException("Ownership context cannot be changed");
        }
        String nextStatus = requireTransition(current.status(), incoming.status());
        return new Record(current.id(), current.reference(), current.slotId(), current.doctorId(),
                current.patientId(), nextStatus, normalizePriority(incoming.priority()), current.createdAt(),
                OperationSupport.now(clock), incoming.dueAt(), incoming.assignedTo(), incoming.description(), current.version() + 1);
    }

    public static Map<String, Object> auditPayload(Record record, String actor, String action, Instant at) {
        Objects.requireNonNull(record, "record");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("domain", DOMAIN); payload.put("recordId", record.id());
        payload.put("reference", record.reference()); payload.put("actor", OperationSupport.required(actor, "actor", 100));
        payload.put("action", OperationSupport.required(action, "action", 60));
        payload.put("status", record.status()); payload.put("version", record.version());
        payload.put("occurredAt", at == null ? Instant.EPOCH : at);
        payload.put("assignedTo", redact(record.assignedTo()));
        return Map.copyOf(payload);
    }

    public static long countChangedSince(Collection<Record> records, Instant since) {
        if (records == null || since == null) return 0;
        return records.stream().filter(Objects::nonNull).filter(r -> !r.updatedAt().isBefore(since)).count();
    }

    public static List<Record> recentlyUpdated(Collection<Record> records, Instant since) {
        if (records == null || since == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> !r.updatedAt().isBefore(since))
                .sorted(Comparator.comparing(Record::updatedAt).reversed()).toList();
    }

    public static boolean isDueOn(Record record, LocalDate date, ZoneId zone) {
        if (record == null || record.dueAt() == null || date == null || zone == null) return false;
        return record.dueAt().atZone(zone).toLocalDate().equals(date);
    }

    public static List<Record> dueOn(Collection<Record> records, LocalDate date, ZoneId zone) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> isDueOn(r, date, zone))
                .sorted(Comparator.comparing(Record::dueAt)).toList();
    }

    public static boolean isFresh(Record record, Instant now, long maxAgeMinutes) {
        if (record == null || now == null || maxAgeMinutes < 0 || now.isBefore(record.updatedAt())) return false;
        return Duration.between(record.updatedAt(), now).toMinutes() <= maxAgeMinutes;
    }

    public static List<Record> staleRecords(Collection<Record> records, Instant now, long maxAgeMinutes) {
        if (records == null || now == null || maxAgeMinutes < 0) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> !isTerminal(r.status()))
                .filter(r -> !isFresh(r, now, maxAgeMinutes)).toList();
    }

    public static String displayStatus(String status) {
        String normalized = normalizeStatus(status);
        String[] words = normalized.toLowerCase(Locale.ROOT).split("_");
        StringBuilder label = new StringBuilder();
        for (String word : words) {
            if (label.length() > 0) label.append(' ');
            label.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return label.toString();
    }

    public static List<String> validationMessages(Record record) {
        return validate(record);
    }

    public static int safeLimit(int requested, int fallback, int maximum) {
        if (maximum < 1 || fallback < 1 || fallback > maximum) throw new IllegalArgumentException("Invalid limit configuration");
        return requested < 1 ? fallback : Math.min(requested, maximum);
    }

    public static BigDecimal ratio(long numerator, long denominator) {
        if (numerator < 0 || denominator < 0) throw new IllegalArgumentException("Counts cannot be negative");
        if (denominator == 0) return BigDecimal.ZERO.setScale(2);
        return BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    public static Map<String, BigDecimal> statusPercentages(Collection<Record> records) {
        List<Record> values = records == null ? List.of() : records.stream().filter(Objects::nonNull).toList();
        Map<String, Long> counts = new LinkedHashMap<>();
        values.forEach(r -> counts.merge(r.status(), 1L, Long::sum));
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        counts.forEach((key,value) -> result.put(key, ratio(value, values.size())));
        return Map.copyOf(result);
    }

    public static boolean isAllowedActor(String actor, Set<String> allowedActors) {
        if (actor == null || actor.isBlank() || allowedActors == null || allowedActors.isEmpty()) return false;
        return allowedActors.stream().filter(Objects::nonNull).anyMatch(a -> a.equalsIgnoreCase(actor.trim()));
    }

    public static String requireReason(String reason, boolean required) {
        String value = OperationSupport.optional(reason, "reason", MAX_NOTE_LENGTH);
        if (required && value.isBlank()) throw new IllegalArgumentException("A reason is required for this action");
        return value;
    }

    public static List<Record> withStatus(Collection<Record> records, String status) {
        if (records == null) return List.of();
        String key = normalizeStatus(status);
        return records.stream().filter(Objects::nonNull).filter(r -> r.status().equals(key)).toList();
    }

    public static List<Record> withPriority(Collection<Record> records, String priority) {
        if (records == null) return List.of();
        String key = normalizePriority(priority);
        return records.stream().filter(Objects::nonNull).filter(r -> r.priority().equals(key)).toList();
    }

    public static long countUnassigned(Collection<Record> records) {
        return records == null ? 0 : records.stream().filter(Objects::nonNull)
                .filter(r -> r.assignedTo().isBlank() && !isTerminal(r.status())).count();
    }

    public static long countTerminal(Collection<Record> records) {
        return records == null ? 0 : records.stream().filter(Objects::nonNull).filter(r -> isTerminal(r.status())).count();
    }

    public static long countActive(Collection<Record> records) {
        return records == null ? 0 : records.stream().filter(Objects::nonNull).filter(r -> isActive(r.status())).count();
    }

    public static List<Record> oldestFirst(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::createdAt)).toList();
    }

    public static List<Record> newestFirst(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::createdAt).reversed()).toList();
    }

    public static boolean matchesOwner(Record record, String owner) {
        return record != null && owner != null && record.doctorId().equalsIgnoreCase(owner.trim());
    }

    public static boolean matchesContext(Record record, String context) {
        return record != null && context != null && record.patientId().equalsIgnoreCase(context.trim());
    }

    public static List<Record> forOwner(Collection<Record> records, String owner) {
        if (records == null || owner == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> matchesOwner(r, owner)).toList();
    }

    public static List<Record> forContext(Collection<Record> records, String context) {
        if (records == null || context == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> matchesContext(r, context)).toList();
    }

    public static Map<String, Long> countsByContext(Collection<Record> records) {
        Map<String, Long> counts = new LinkedHashMap<>();
        if (records == null) return counts;
        records.stream().filter(Objects::nonNull).forEach(r -> counts.merge(r.patientId(), 1L, Long::sum));
        return Map.copyOf(counts);
    }

    public static Map<String, Long> countsByOwner(Collection<Record> records) {
        Map<String, Long> counts = new LinkedHashMap<>();
        if (records == null) return counts;
        records.stream().filter(Objects::nonNull).forEach(r -> counts.merge(r.doctorId(), 1L, Long::sum));
        return Map.copyOf(counts);
    }

    public static Record requireRecord(Record record) {
        if (record == null) throw new IllegalArgumentException("record is required");
        List<String> errors = validate(record);
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join("; ", errors));
        return record;
    }

    public static List<Record> removeTerminal(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> !isTerminal(r.status())).toList();
    }

    public static List<Record> removeExpired(Collection<Record> records, Instant now) {
        if (records == null || now == null) return List.of();
        return records.stream().filter(Objects::nonNull)
                .filter(r -> r.dueAt() == null || !r.dueAt().isBefore(now) || isTerminal(r.status())).toList();
    }

    public static Map<String, Object> operationalSnapshot(Collection<Record> records, Instant now) {
        Map<String, Object> snapshot = new LinkedHashMap<>(dashboard(records, now));
        snapshot.put("unassigned", countUnassigned(records));
        snapshot.put("critical", countPriority(records, "CRITICAL"));
        snapshot.put("urgent", countPriority(records, "URGENT"));
        snapshot.put("completionPercent", completionRate(records));
        snapshot.put("overduePercent", overdueRate(records, now).value());
        return Map.copyOf(snapshot);
    }

    public static List<Record> topPriority(Collection<Record> records, int limit) {
        if (records == null || limit < 1) return List.of();
        return records.stream().filter(Objects::nonNull)
                .sorted((a,b) -> {
                    int c = comparePriority(a.priority(), b.priority());
                    if (c != 0) return c;
                    return a.createdAt().compareTo(b.createdAt());
                }).limit(Math.min(limit, MAX_PAGE_SIZE)).toList();
    }

    public static boolean isEditable(Record record, boolean administrator) {
        if (record == null) return false;
        return administrator || !isTerminal(record.status());
    }

    public static String versionTag(Record record) {
        Objects.requireNonNull(record, "record");
        return record.id() + ":v" + record.version();
    }

    public static boolean hasVersionTag(Record record, String tag) {
        return record != null && tag != null && versionTag(record).equals(tag.trim());
    }

    public static Map<String, Object> toMap(Record record) {
        Objects.requireNonNull(record, "record");
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", record.id()); map.put("reference", record.reference());
        map.put("itemId", record.slotId()); map.put("owner", record.doctorId());
        map.put("context", record.patientId()); map.put("status", record.status());
        map.put("priority", record.priority()); map.put("createdAt", record.createdAt());
        map.put("updatedAt", record.updatedAt()); map.put("dueAt", record.dueAt());
        map.put("assignedTo", record.assignedTo()); map.put("description", record.description());
        map.put("version", record.version());
        return Map.copyOf(map);
    }

    public static List<Record> search(Collection<Record> records, String query) {
        if (records == null) return List.of();
        String needle = OperationSupport.search(query);
        if (needle.isEmpty()) return records.stream().filter(Objects::nonNull).toList();
        return records.stream().filter(Objects::nonNull).filter(r -> searchable(r).contains(needle)).toList();
    }

    public static Map<String, Long> weeklyCreatedCounts(Collection<Record> records, ZoneId zone) {
        if (records == null || zone == null) return Map.of();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Record record : records) {
            if (record == null) continue;
            LocalDate day = record.createdAt().atZone(zone).toLocalDate();
            LocalDate monday = day.minusDays(day.getDayOfWeek().getValue() - 1L);
            result.merge(monday.toString(), 1L, Long::sum);
        }
        return Map.copyOf(result);
    }

    public static Map<String, Object> recordFingerprint(Record record) {
        Objects.requireNonNull(record, "record");
        Map<String, Object> fingerprint = new LinkedHashMap<>();
        fingerprint.put("id", record.id()); fingerprint.put("version", record.version());
        fingerprint.put("status", record.status()); fingerprint.put("updatedAt", record.updatedAt());
        fingerprint.put("reference", record.reference());
        return Map.copyOf(fingerprint);
    }

    public static boolean sameBusinessIdentity(Record a, Record b) {
        return a != null && b != null && a.reference().equalsIgnoreCase(b.reference())
                && a.slotId().equals(b.slotId());
    }

    public static List<Record> distinctByReference(Collection<Record> records) {
        if (records == null) return List.of();
        Map<String, Record> unique = new LinkedHashMap<>();
        records.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Record::updatedAt).reversed())
                .forEach(r -> unique.putIfAbsent(r.reference().toLowerCase(Locale.ROOT), r));
        return List.copyOf(unique.values());
    }

    public static List<Record> createdBetween(Collection<Record> records, LocalDate from, LocalDate to, ZoneId zone) {
        if (records == null || zone == null) return List.of();
        if (from != null && to != null && to.isBefore(from)) throw new IllegalArgumentException("Invalid date range");
        return records.stream().filter(Objects::nonNull)
                .filter(r -> OperationSupport.within(r.createdAt().atZone(zone).toLocalDate(), from, to))
                .sorted(Comparator.comparing(Record::createdAt)).toList();
    }

    public static long countCreatedOn(Collection<Record> records, LocalDate date, ZoneId zone) {
        if (records == null || date == null || zone == null) return 0;
        return records.stream().filter(Objects::nonNull)
                .filter(r -> r.createdAt().atZone(zone).toLocalDate().equals(date)).count();
    }

    public static String actorLabel(String actor) {
        String value = OperationSupport.optional(actor, "actor", 100);
        return value.isBlank() ? "System" : value;
    }

    public static boolean requiresEscalation(Record record, Instant now) {
        return record != null && !isTerminal(record.status())
                && (isOverdue(record, now) || "CRITICAL".equals(record.priority()));
    }

    public static List<Record> escalationCandidates(Collection<Record> records, Instant now) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull).filter(r -> requiresEscalation(r, now))
                .sorted(Comparator.comparing((Record r) -> workloadScore(r, now)).reversed()).toList();
    }

    public static BigDecimal medianAgeMinutes(Collection<Record> records, Instant now) {
        if (records == null || records.isEmpty()) return BigDecimal.ZERO;
        List<Long> ages = records.stream().filter(Objects::nonNull).map(r -> ageMinutes(r, now)).sorted().toList();
        int mid = ages.size() / 2;
        if (ages.size() % 2 == 1) return BigDecimal.valueOf(ages.get(mid));
        return BigDecimal.valueOf(ages.get(mid - 1) + ages.get(mid)).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    public static List<Record> newestDueFirst(Collection<Record> records) {
        if (records == null) return List.of();
        return records.stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(Record::dueAt, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
    }

    public static boolean canDelete(Record record) {
        return record != null && record.version() == 0 && record.status().equals("CONFIRMED");
    }

    public static String requireStatus(String value) {
        String normalized = normalizeStatus(value);
        if (!STATES.contains(normalized)) throw new IllegalArgumentException("Unsupported {code} status: " + normalized);
        return normalized;
    }

    public static List<String> transitionTargets(String current) {
        return TRANSITIONS.getOrDefault(normalizeStatus(current), Set.of()).stream().sorted().toList();
    }

    public static boolean isValidTransitionPath(List<String> path) {
        if (path == null || path.isEmpty()) return false;
        for (String state : path) if (!STATES.contains(normalizeStatus(state))) return false;
        for (int i = 1; i < path.size(); i++) if (!canTransition(path.get(i - 1), path.get(i))) return false;
        return true;
    }

    public static String describeTransition(String from, String to) {
        String a = displayStatus(from);
        String b = displayStatus(to);
        return a + " → " + b;
    }

    public static Map<String, Object> policyDescriptor() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("domain", DOMAIN); result.put("initialState", "CONFIRMED");
        result.put("states", supportedStates()); result.put("terminalStates", terminalStates());
        result.put("maxPageSize", MAX_PAGE_SIZE); result.put("maxNoteLength", MAX_NOTE_LENGTH);
        return Map.copyOf(result);
    }
}
