package com.healthcare.hospital.enterprise.quality.repository;

import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QualityReviewRepository extends JpaRepository<QualityReview,Long> {
    Optional<QualityReview> findByCaseReferenceIgnoreCase(String reference);
    boolean existsByCaseReferenceIgnoreCase(String reference);
    List<QualityReview> findTop200ByOrderByUpdatedAtDesc();
    List<QualityReview> findByStatusOrderByDueDateAsc(String status);
    List<QualityReview> findByOwnerUsernameIgnoreCaseOrderByDueDateAsc(String owner);
    List<QualityReview> findByPatientReferenceIgnoreCaseOrderByReportedAtDesc(String patientReference);
    long countByStatus(String status);
    long countBySeverityAndStatusNotIn(String severity, List<String> excluded);
    long countByDueDateBeforeAndStatusNotIn(LocalDate date, List<String> excluded);
    @Query("select q from QualityReview q where " +
           "(:status is null or q.status=:status) and " +
           "(:severity is null or q.severity=:severity) and " +
           "(:category is null or q.category=:category) and " +
           "(:owner is null or lower(q.ownerUsername)=lower(:owner)) and " +
           "(:fromDate is null or q.eventDate>=:fromDate) and " +
           "(:toDate is null or q.eventDate<=:toDate) and " +
           "(:term is null or lower(q.caseReference) like lower(concat('%',:term,'%')) or " +
           "lower(q.title) like lower(concat('%',:term,'%')) or " +
           "lower(coalesce(q.patientReference,'')) like lower(concat('%',:term,'%')))")
    Page<QualityReview> search(@Param("status") String status, @Param("severity") String severity,
        @Param("category") String category, @Param("owner") String owner,
        @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
        @Param("term") String term, Pageable pageable);
}
