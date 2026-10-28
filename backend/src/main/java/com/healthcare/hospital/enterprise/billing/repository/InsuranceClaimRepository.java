package com.healthcare.hospital.enterprise.billing.repository;

import com.healthcare.hospital.enterprise.billing.entity.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for insurance claim lifecycle. */
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    List<InsuranceClaim> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<InsuranceClaim> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<InsuranceClaim> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<InsuranceClaim> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<InsuranceClaim> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from InsuranceClaim e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from InsuranceClaim e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<InsuranceClaim> search(@Param("status") String status, @Param("assignee") String assignee);
}
