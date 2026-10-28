package com.healthcare.hospital.enterprise.billing.repository;

import com.healthcare.hospital.enterprise.billing.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for billing refund approvals. */
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<RefundRequest> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<RefundRequest> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<RefundRequest> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<RefundRequest> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from RefundRequest e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from RefundRequest e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<RefundRequest> search(@Param("status") String status, @Param("assignee") String assignee);
}
