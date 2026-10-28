package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.CareTask;
import com.healthcare.hospital.repository.CareTaskRepository;
import com.healthcare.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CareTaskService {
    private static final Set<String> PRIORITIES = Set.of("LOW", "NORMAL", "HIGH", "URGENT");
    private static final Set<String> TYPES = Set.of("VITALS", "MEDICATION", "OBSERVATION", "HYGIENE", "TRANSFER", "OTHER");
    private final CareTaskRepository tasks;
    private final PatientRepository patients;

    public CareTaskService(CareTaskRepository tasks, PatientRepository patients) {
        this.tasks = tasks; this.patients = patients;
    }

    @Transactional
    public CareTask create(String title, String type, String priority, Long patientId,
                           String assignee, String ward, LocalDateTime dueAt, String instructions) {
        String cleanTitle = required(title, 120, "Task title");
        String cleanType = required(type, 40, "Task type").toUpperCase();
        String cleanPriority = required(priority, 20, "Priority").toUpperCase();
        if (!TYPES.contains(cleanType)) throw new IllegalArgumentException("Unsupported care task type.");
        if (!PRIORITIES.contains(cleanPriority)) throw new IllegalArgumentException("Unsupported priority.");
        CareTask task = new CareTask();
        task.setTitle(cleanTitle);
        task.setTaskType(cleanType);
        task.setPriority(cleanPriority);
        task.setStatus("OPEN");
        if (patientId != null) task.setPatient(patients.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient was not found.")));
        task.setAssignedTo(optional(assignee, 100));
        task.setWard(optional(ward, 100));
        task.setDueAt(dueAt);
        task.setInstructions(optional(instructions, 1500));
        return tasks.save(task);
    }

    @Transactional
    public CareTask changeStatus(Long id, String status, String actor) {
        CareTask task = tasks.findById(id).orElseThrow(() -> new IllegalArgumentException("Care task was not found."));
        String next = required(status, 24, "Status").toUpperCase();
        if (!Set.of("OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED").contains(next)) {
            throw new IllegalArgumentException("Unsupported care task status.");
        }
        if ("COMPLETED".equals(task.getStatus()) || "CANCELLED".equals(task.getStatus())) {
            throw new IllegalStateException("A closed care task cannot be changed.");
        }
        task.setStatus(next);
        if ("COMPLETED".equals(next)) {
            task.setCompletedAt(LocalDateTime.now());
            task.setCompletedBy(optional(actor, 100));
        }
        return tasks.save(task);
    }

    @Transactional(readOnly = true)
    public List<CareTask> all() { return tasks.findAllByOrderByDueAtAsc(); }

    private String required(String value, int max, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required.");
        String clean = value.trim();
        if (clean.length() > max) throw new IllegalArgumentException(label + " is too long.");
        return clean;
    }
    private String optional(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String clean = value.trim();
        if (clean.length() > max) throw new IllegalArgumentException("Field is too long.");
        return clean;
    }
}
