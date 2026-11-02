package com.healthcare.hospital.modules.doctor.dashboard;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
/** Read-only snapshot of operational counts supplied by the authenticated clinical workspace. */
public final class DoctorDashboardSignalSnapshot {
    private final Map<String, Double> values;
    private final Instant capturedAt;
    public DoctorDashboardSignalSnapshot(Map<String, Double> values, Instant capturedAt) {
        Map<String, Double> safe = new LinkedHashMap<>();
        if (values != null) values.forEach((key,value) -> {
            if (key != null && !key.isBlank() && value != null && Double.isFinite(value)) safe.put(key, value);
        });
        this.values = Collections.unmodifiableMap(safe);
        this.capturedAt = capturedAt == null ? Instant.now() : capturedAt;
    }
    public static DoctorDashboardSignalSnapshot empty() { return new DoctorDashboardSignalSnapshot(Map.of(), Instant.now()); }
    public double value(String metric) { return values.getOrDefault(metric, 0.0); }
    public boolean contains(String metric) { return values.containsKey(metric); }
    public Map<String, Double> values() { return values; }
    public Instant capturedAt() { return capturedAt; }
    public int size() { return values.size(); }
}
