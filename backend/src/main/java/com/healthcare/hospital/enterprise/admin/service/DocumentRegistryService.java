package com.healthcare.hospital.enterprise.admin.service;

import com.healthcare.hospital.enterprise.admin.entity.DocumentRegistry;
import com.healthcare.hospital.enterprise.admin.repository.DocumentRegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/** Transaction boundary and lifecycle rules for medical document registry metadata. */
@Service
@Transactional
public class DocumentRegistryService {
    private final DocumentRegistryRepository repository;

    public DocumentRegistryService(DocumentRegistryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DocumentRegistry> list(String status, String assignee) {
        if ((status == null || status.isBlank()) && (assignee == null || assignee.isBlank())) {
            return repository.findTop100ByOrderByCreatedAtDesc();
        }
        return repository.search(blankToNull(status), blankToNull(assignee));
    }

    @Transactional(readOnly = true)
    public DocumentRegistry get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Record not found: " + id));
    }

    public DocumentRegistry create(DocumentRegistry record, String actor) {
        if (record == null) throw new IllegalArgumentException("Record is required");
        record.setId(null);
        record.setCreatedBy(actor);
        record.setCreatedAt(java.time.LocalDateTime.now());
        if (record.getStatus() == null || record.getStatus().isBlank()) record.setStatus("OPEN");
        record.validateRequiredFields();
        return repository.save(record);
    }

    public DocumentRegistry update(Long id, DocumentRegistry input, String actor) {
        DocumentRegistry current = get(id);
        if (current.isClosed()) throw new IllegalStateException("Closed records cannot be edited");
        if (input == null) throw new IllegalArgumentException("Updated record is required");
        current.setTitle(input.getTitle());
        current.setDescription(input.getDescription());
        current.setReferenceCode(input.getReferenceCode());
        current.setAssignedTo(input.getAssignedTo());
        current.setDueDate(input.getDueDate());
        current.setAmount(input.getAmount());
        current.setUpdatedBy(actor);
        current.validateRequiredFields();
        return repository.save(current);
    }

    public DocumentRegistry start(Long id, String actor) {
        DocumentRegistry record = get(id);
        record.markInProgress(actor);
        return repository.save(record);
    }

    public DocumentRegistry complete(Long id, String actor) {
        DocumentRegistry record = get(id);
        record.complete(actor);
        return repository.save(record);
    }

    public DocumentRegistry cancel(Long id, String actor, String reason) {
        DocumentRegistry record = get(id);
        record.cancel(actor, reason);
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<DocumentRegistry> overdue() {
        return repository.findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate.now(), "COMPLETED");
    }

    @Transactional(readOnly = true)
    public long countAll() { return repository.count(); }

    @Transactional(readOnly = true)
    public long countOpen() { return repository.countByStatus("OPEN") + repository.countByStatus("PENDING"); }

    @Transactional(readOnly = true)
    public long countCompleted() { return repository.countByStatus("COMPLETED"); }

    @Transactional(readOnly = true)
    public long countOverdue() { return overdue().size(); }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
