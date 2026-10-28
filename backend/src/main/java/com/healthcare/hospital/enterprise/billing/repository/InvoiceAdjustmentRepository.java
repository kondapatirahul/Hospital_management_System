package com.healthcare.hospital.enterprise.billing.repository;

import com.healthcare.hospital.enterprise.billing.entity.InvoiceAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for invoice adjustment requests. */
public interface InvoiceAdjustmentRepository extends JpaRepository<InvoiceAdjustment, Long> {
    List<InvoiceAdjustment> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<InvoiceAdjustment> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<InvoiceAdjustment> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<InvoiceAdjustment> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<InvoiceAdjustment> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from InvoiceAdjustment e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from InvoiceAdjustment e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<InvoiceAdjustment> search(@Param("status") String status, @Param("assignee") String assignee);
}
