package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SafetyIncidentForm;
import com.healthcare.hospital.entity.SafetyIncident;
import com.healthcare.hospital.repository.SafetyIncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Business rules for a safety-event lifecycle. No status change bypasses this service. */
@Service
@Transactional
public class SafetyIncidentService {
    private static final Set<String> SEVERITIES = Set.of("LOW", "MODERATE", "HIGH", "CRITICAL");
    private static final Set<String> STATUSES = Set.of("REPORTED", "ACKNOWLEDGED", "INVESTIGATING", "ACTION_REQUIRED", "RESOLVED", "CLOSED");
    private static final Set<String> CATEGORIES = Set.of("PATIENT_FALL", "MEDICATION", "INFECTION_CONTROL", "EQUIPMENT", "IDENTIFICATION", "DOCUMENTATION", "NEAR_MISS", "STAFF_SAFETY", "ENVIRONMENT", "OTHER");
    private final SafetyIncidentRepository repository;

    public SafetyIncidentService(SafetyIncidentRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<SafetyIncident> all() { return repository.findAllByOrderByReportedAtDesc(); }

    @Transactional(readOnly = true)
    public Optional<SafetyIncident> find(long id) { return repository.findById(id); }

    @Transactional(readOnly = true)
    public List<SafetyIncident> search(String status, String severity, String department, String term) {
        return repository.search(cleanFilter(status, STATUSES), cleanFilter(severity, SEVERITIES),
            blankToNull(department), blankToNull(term));
    }

    public SafetyIncident report(SafetyIncidentForm form, String actor) {
        Objects.requireNonNull(form, "Incident form is required");
        validate(form.getSeverity(), SEVERITIES, "severity");
        validate(form.getCategory(), CATEGORIES, "category");
        SafetyIncident incident = new SafetyIncident();
        incident.setReferenceCode(nextReference());
        incident.setTitle(required(form.getTitle(), "Title", 140));
        incident.setCategory(form.getCategory());
        incident.setSeverity(form.getSeverity());
        incident.setDepartment(required(form.getDepartment(), "Department", 100));
        incident.setReportedBy(required(actor, "Reporter", 100));
        incident.setAssignedTo(blankToNull(form.getAssignedTo()));
        incident.setPatientReference(blankToNull(form.getPatientReference()));
        incident.setLocation(blankToNull(form.getLocation()));
        incident.setDescription(required(form.getDescription(), "Description", 3000));
        incident.setImmediateAction(blankToNull(form.getImmediateAction()));
        incident.setOccurredAt(form.getOccurredAt() == null ? LocalDateTime.now() : form.getOccurredAt());
        incident.setReportedAt(LocalDateTime.now());
        incident.setConfidential(form.isConfidential());
        incident.setStatus("REPORTED");
        return repository.save(incident);
    }

    public SafetyIncident acknowledge(long id, String actor) {
        SafetyIncident incident = require(id);
        requireTransition(incident, Set.of("REPORTED"), "ACKNOWLEDGED");
        incident.setStatus("ACKNOWLEDGED");
        incident.setAcknowledgedBy(required(actor, "Actor", 100));
        incident.setAcknowledgedAt(LocalDateTime.now());
        return repository.save(incident);
    }

    public SafetyIncident beginInvestigation(long id, String actor, String assignee) {
        SafetyIncident incident = require(id);
        requireTransition(incident, Set.of("REPORTED", "ACKNOWLEDGED"), "INVESTIGATING");
        incident.setStatus("INVESTIGATING");
        if (blankToNull(assignee) != null) incident.setAssignedTo(assignee.trim());
        if (incident.getAcknowledgedAt() == null) {
            incident.setAcknowledgedAt(LocalDateTime.now());
            incident.setAcknowledgedBy(required(actor, "Actor", 100));
        }
        return repository.save(incident);
    }

    public SafetyIncident saveInvestigation(long id, String rootCause, String correctiveAction,
                                           String preventionPlan, String notes) {
        SafetyIncident incident = require(id);
        if (!Set.of("ACKNOWLEDGED", "INVESTIGATING", "ACTION_REQUIRED").contains(incident.getStatus()))
            throw new IllegalStateException("Investigation details can only be changed while the incident is active.");
        incident.setRootCause(limit(rootCause, 3000));
        incident.setCorrectiveAction(limit(correctiveAction, 3000));
        incident.setPreventionPlan(limit(preventionPlan, 3000));
        incident.setReviewNotes(limit(notes, 2000));
        if (blankToNull(correctiveAction) != null || blankToNull(preventionPlan) != null)
            incident.setStatus("ACTION_REQUIRED");
        return repository.save(incident);
    }

    public SafetyIncident resolve(long id, String actor, String resolutionSummary) {
        SafetyIncident incident = require(id);
        requireTransition(incident, Set.of("INVESTIGATING", "ACTION_REQUIRED", "ACKNOWLEDGED"), "RESOLVED");
        if (blankToNull(resolutionSummary) == null)
            throw new IllegalArgumentException("Add a resolution summary before resolving the incident.");
        incident.setReviewNotes(limit(resolutionSummary, 2000));
        incident.setResolvedBy(required(actor, "Actor", 100));
        incident.setResolvedAt(LocalDateTime.now());
        incident.setStatus("RESOLVED");
        return repository.save(incident);
    }

    public SafetyIncident close(long id, String actor, String closureNote) {
        SafetyIncident incident = require(id);
        requireTransition(incident, Set.of("RESOLVED"), "CLOSED");
        if (blankToNull(closureNote) != null) incident.setReviewNotes(limit(closureNote, 2000));
        incident.setStatus("CLOSED");
        incident.setResolvedBy(required(actor, "Actor", 100));
        return repository.save(incident);
    }

    public SafetyIncident reassign(long id, String assignee) {
        SafetyIncident incident = require(id);
        if (Set.of("RESOLVED", "CLOSED").contains(incident.getStatus()))
            throw new IllegalStateException("Closed incidents cannot be reassigned.");
        incident.setAssignedTo(required(assignee, "Assignee", 100));
        return repository.save(incident);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> metrics() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", repository.count());
        result.put("open", repository.countByStatus("REPORTED") + repository.countByStatus("ACKNOWLEDGED")
            + repository.countByStatus("INVESTIGATING") + repository.countByStatus("ACTION_REQUIRED"));
        result.put("critical", repository.countBySeverity("CRITICAL"));
        result.put("high", repository.countBySeverity("HIGH"));
        result.put("resolved", repository.countByStatus("RESOLVED") + repository.countByStatus("CLOSED"));
        LocalDate today = LocalDate.now();
        result.put("today", repository.countByReportedAtBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay()));
        result.put("departments", summarize(repository.countByDepartment()));
        result.put("categories", summarize(repository.countByCategory()));
        result.put("severity", summarize(repository.countBySeverityGrouped()));
        return result;
    }

    @Transactional(readOnly = true)
    public List<SafetyIncident> exportRows(String status, String severity, String department, String term) {
        return search(status, severity, department, term);
    }

    private List<Map<String, Object>> summarize(List<Object[]> rows) {
        List<Map<String, Object>> values = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("label", String.valueOf(row[0]));
            value.put("count", ((Number) row[1]).longValue());
            values.add(value);
        }
        return values;
    }

    private SafetyIncident require(long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));
    }
    private void requireTransition(SafetyIncident incident, Set<String> allowed, String next) {
        if (!allowed.contains(incident.getStatus()))
            throw new IllegalStateException("Cannot move incident from " + incident.getStatus() + " to " + next + ".");
    }
    private String nextReference() {
        String prefix = "SAFE-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        String candidate;
        int attempt = 0;
        do { candidate = prefix + String.format("%04d", (System.nanoTime() + attempt++) % 10000); }
        while (repository.existsByReferenceCode(candidate));
        return candidate;
    }
    private static String required(String value, String label, int max) {
        String cleaned = blankToNull(value);
        if (cleaned == null) throw new IllegalArgumentException(label + " is required.");
        if (cleaned.length() > max) throw new IllegalArgumentException(label + " must be at most " + max + " characters.");
        return cleaned;
    }
    private static String limit(String value, int max) {
        if (value == null) return null;
        String cleaned = value.trim();
        if (cleaned.length() > max) throw new IllegalArgumentException("Text must be at most " + max + " characters.");
        return cleaned.isEmpty() ? null : cleaned;
    }
    private static String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private static void validate(String value, Set<String> allowed, String label) {
        if (value == null || !allowed.contains(value)) throw new IllegalArgumentException("Select a valid " + label + ".");
    }
    private static String cleanFilter(String value, Set<String> allowed) {
        String cleaned = blankToNull(value);
        return cleaned == null ? null : (allowed.contains(cleaned) ? cleaned : null);
    }
}
