package com.healthcare.hospital.enterprise.billing.service;

import com.healthcare.hospital.enterprise.billing.entity.PaymentReconciliation;
import com.healthcare.hospital.enterprise.billing.repository.PaymentReconciliationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/** Transaction boundary and lifecycle rules for daily payment reconciliation. */
@Service
@Transactional
public class PaymentReconciliationService {
    private final PaymentReconciliationRepository repository;

    public PaymentReconciliationService(PaymentReconciliationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PaymentReconciliation> list(String status, String assignee) {
        if ((status == null || status.isBlank()) && (assignee == null || assignee.isBlank())) {
            return repository.findTop100ByOrderByCreatedAtDesc();
        }
        return repository.search(blankToNull(status), blankToNull(assignee));
    }

    @Transactional(readOnly = true)
    public PaymentReconciliation get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Record not found: " + id));
    }

    public PaymentReconciliation create(PaymentReconciliation record, String actor) {
        if (record == null) throw new IllegalArgumentException("Record is required");
        record.setId(null);
        record.setCreatedBy(actor);
        record.setCreatedAt(java.time.LocalDateTime.now());
        if (record.getStatus() == null || record.getStatus().isBlank()) record.setStatus("OPEN");
        record.validateRequiredFields();
        return repository.save(record);
    }

    public PaymentReconciliation update(Long id, PaymentReconciliation input, String actor) {
        PaymentReconciliation current = get(id);
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

    public PaymentReconciliation start(Long id, String actor) {
        PaymentReconciliation record = get(id);
        record.markInProgress(actor);
        return repository.save(record);
    }

    public PaymentReconciliation complete(Long id, String actor) {
        PaymentReconciliation record = get(id);
        record.complete(actor);
        return repository.save(record);
    }

    public PaymentReconciliation cancel(Long id, String actor, String reason) {
        PaymentReconciliation record = get(id);
        record.cancel(actor, reason);
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<PaymentReconciliation> overdue() {
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
