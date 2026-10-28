package com.healthcare.hospital.enterprise.billing.repository;

import com.healthcare.hospital.enterprise.billing.entity.PaymentReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for daily payment reconciliation. */
public interface PaymentReconciliationRepository extends JpaRepository<PaymentReconciliation, Long> {
    List<PaymentReconciliation> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PaymentReconciliation> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<PaymentReconciliation> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<PaymentReconciliation> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<PaymentReconciliation> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from PaymentReconciliation e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from PaymentReconciliation e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<PaymentReconciliation> search(@Param("status") String status, @Param("assignee") String assignee);
}
