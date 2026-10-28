package com.healthcare.hospital.enterprise.pharmacy.service;

import com.healthcare.hospital.enterprise.pharmacy.entity.PharmacyStocktake;
import com.healthcare.hospital.enterprise.pharmacy.repository.PharmacyStocktakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/** Transaction boundary and lifecycle rules for pharmacy stocktake sessions. */
@Service
@Transactional
public class PharmacyStocktakeService {
    private final PharmacyStocktakeRepository repository;

    public PharmacyStocktakeService(PharmacyStocktakeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PharmacyStocktake> list(String status, String assignee) {
        if ((status == null || status.isBlank()) && (assignee == null || assignee.isBlank())) {
            return repository.findTop100ByOrderByCreatedAtDesc();
        }
        return repository.search(blankToNull(status), blankToNull(assignee));
    }

    @Transactional(readOnly = true)
    public PharmacyStocktake get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Record not found: " + id));
    }

    public PharmacyStocktake create(PharmacyStocktake record, String actor) {
        if (record == null) throw new IllegalArgumentException("Record is required");
        record.setId(null);
        record.setCreatedBy(actor);
        record.setCreatedAt(java.time.LocalDateTime.now());
        if (record.getStatus() == null || record.getStatus().isBlank()) record.setStatus("OPEN");
        record.validateRequiredFields();
        return repository.save(record);
    }

    public PharmacyStocktake update(Long id, PharmacyStocktake input, String actor) {
        PharmacyStocktake current = get(id);
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

    public PharmacyStocktake start(Long id, String actor) {
        PharmacyStocktake record = get(id);
        record.markInProgress(actor);
        return repository.save(record);
    }

    public PharmacyStocktake complete(Long id, String actor) {
        PharmacyStocktake record = get(id);
        record.complete(actor);
        return repository.save(record);
    }

    public PharmacyStocktake cancel(Long id, String actor, String reason) {
        PharmacyStocktake record = get(id);
        record.cancel(actor, reason);
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<PharmacyStocktake> overdue() {
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
