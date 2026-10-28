package com.healthcare.hospital.enterprise.laboratory.repository;

import com.healthcare.hospital.enterprise.laboratory.entity.LabReagent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for laboratory reagent inventory. */
public interface LabReagentRepository extends JpaRepository<LabReagent, Long> {
    List<LabReagent> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<LabReagent> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<LabReagent> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<LabReagent> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<LabReagent> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from LabReagent e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from LabReagent e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<LabReagent> search(@Param("status") String status, @Param("assignee") String assignee);
}
