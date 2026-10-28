package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.DischargeChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for discharge readiness checklist. */
public interface DischargeChecklistRepository extends JpaRepository<DischargeChecklist, Long> {
    List<DischargeChecklist> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<DischargeChecklist> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<DischargeChecklist> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<DischargeChecklist> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<DischargeChecklist> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from DischargeChecklist e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from DischargeChecklist e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<DischargeChecklist> search(@Param("status") String status, @Param("assignee") String assignee);
}
