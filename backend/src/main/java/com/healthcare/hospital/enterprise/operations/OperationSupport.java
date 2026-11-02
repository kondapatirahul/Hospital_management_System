package com.healthcare.hospital.enterprise.operations;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class OperationSupport {
    private OperationSupport() { throw new IllegalStateException("No instances"); }

    public static String required(String value, String field, int max) {
        if (value == null) throw new IllegalArgumentException(field + " is required");
        String clean = value.trim();
        if (clean.isEmpty()) throw new IllegalArgumentException(field + " is required");
        if (clean.length() > max) throw new IllegalArgumentException(field + " exceeds " + max + " characters");
        if (clean.indexOf('\0') >= 0) throw new IllegalArgumentException(field + " contains a null character");
        return clean;
    }

    public static String optional(String value, String field, int max) {
        if (value == null) return "";
        String clean = value.trim();
        if (clean.length() > max) throw new IllegalArgumentException(field + " exceeds " + max + " characters");
        if (clean.indexOf('\0') >= 0) throw new IllegalArgumentException(field + " contains a null character");
        return clean;
    }

    public static String reference(String value, String field) {
        String ref = required(value, field, 80);
        if (!ref.matches("[A-Za-z0-9][A-Za-z0-9._/-]{1,79}")) {
            throw new IllegalArgumentException(field + " contains unsupported characters");
        }
        return ref;
    }

    public static String status(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z][A-Z0-9_]{0,39}")) throw new IllegalArgumentException("Invalid status");
        return normalized;
    }

    public static int page(int value) { return Math.max(0, value); }
    public static int pageSize(int value) { return value < 1 ? 25 : Math.min(value, 500); }
    public static Instant now(Clock clock) { return Objects.requireNonNull(clock, "clock").instant(); }
    public static LocalDate today(Clock clock, ZoneId zone) {
        return LocalDate.now(Objects.requireNonNull(clock, "clock").withZone(Objects.requireNonNull(zone, "zone")));
    }
    public static String id() { return UUID.randomUUID().toString(); }
    public static boolean within(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) return false;
        return (from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to));
    }
    public static String search(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
    public static boolean contains(String value, String query) {
        String needle = search(query);
        return needle.isEmpty() || (value != null && search(value).contains(needle));
    }
}
