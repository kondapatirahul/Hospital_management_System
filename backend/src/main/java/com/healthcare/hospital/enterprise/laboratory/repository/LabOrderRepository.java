package com.healthcare.hospital.enterprise.laboratory.repository;

import com.healthcare.hospital.enterprise.laboratory.entity.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for laboratory order intake and status tracking. */
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    List<LabOrder> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<LabOrder> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<LabOrder> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<LabOrder> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<LabOrder> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from LabOrder e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from LabOrder e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<LabOrder> search(@Param("status") String status, @Param("assignee") String assignee);
}
