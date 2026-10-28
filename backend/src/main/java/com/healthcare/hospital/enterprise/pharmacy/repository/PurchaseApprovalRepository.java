package com.healthcare.hospital.enterprise.pharmacy.repository;

import com.healthcare.hospital.enterprise.pharmacy.entity.PurchaseApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for purchase approval workflow. */
public interface PurchaseApprovalRepository extends JpaRepository<PurchaseApproval, Long> {
    List<PurchaseApproval> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PurchaseApproval> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<PurchaseApproval> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<PurchaseApproval> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<PurchaseApproval> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from PurchaseApproval e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from PurchaseApproval e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<PurchaseApproval> search(@Param("status") String status, @Param("assignee") String assignee);
}
