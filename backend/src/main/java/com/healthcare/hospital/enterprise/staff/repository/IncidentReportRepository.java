package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for clinical incident reporting. */
public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
    List<IncidentReport> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<IncidentReport> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<IncidentReport> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<IncidentReport> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<IncidentReport> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from IncidentReport e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from IncidentReport e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<IncidentReport> search(@Param("status") String status, @Param("assignee") String assignee);
}
