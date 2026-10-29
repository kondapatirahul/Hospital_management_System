package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.SafetyIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Query boundary for incident review, triage and audit views. */
public interface SafetyIncidentRepository extends JpaRepository<SafetyIncident, Long> {
    Optional<SafetyIncident> findByReferenceCode(String referenceCode);
    boolean existsByReferenceCode(String referenceCode);
    List<SafetyIncident> findAllByOrderByReportedAtDesc();
    List<SafetyIncident> findByStatusOrderBySeverityDescReportedAtAsc(String status);
    List<SafetyIncident> findByDepartmentIgnoreCaseOrderByReportedAtDesc(String department);
    List<SafetyIncident> findByAssignedToIgnoreCaseOrderByReportedAtDesc(String assignedTo);
    long countByStatus(String status);
    long countBySeverity(String severity);
    long countByDepartmentIgnoreCase(String department);
    long countByReportedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatusAndSeverity(String status, String severity);

    @Query("select i from SafetyIncident i where " +
        "(:status is null or i.status = :status) and " +
        "(:severity is null or i.severity = :severity) and " +
        "(:department is null or lower(i.department) = lower(:department)) and " +
        "(:term is null or lower(i.referenceCode) like lower(concat('%',:term,'%')) " +
        "or lower(i.title) like lower(concat('%',:term,'%')) " +
        "or lower(i.category) like lower(concat('%',:term,'%'))) " +
        "order by i.reportedAt desc")
    List<SafetyIncident> search(@Param("status") String status,
        @Param("severity") String severity, @Param("department") String department,
        @Param("term") String term);

    @Query("select i.department, count(i) from SafetyIncident i group by i.department order by count(i) desc")
    List<Object[]> countByDepartment();

    @Query("select i.category, count(i) from SafetyIncident i group by i.category order by count(i) desc")
    List<Object[]> countByCategory();

    @Query("select i.severity, count(i) from SafetyIncident i group by i.severity")
    List<Object[]> countBySeverityGrouped();
}
