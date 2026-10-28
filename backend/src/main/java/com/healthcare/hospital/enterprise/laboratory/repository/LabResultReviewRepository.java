package com.healthcare.hospital.enterprise.laboratory.repository;

import com.healthcare.hospital.enterprise.laboratory.entity.LabResultReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for laboratory result review and authorization. */
public interface LabResultReviewRepository extends JpaRepository<LabResultReview, Long> {
    List<LabResultReview> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<LabResultReview> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<LabResultReview> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<LabResultReview> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<LabResultReview> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from LabResultReview e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from LabResultReview e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<LabResultReview> search(@Param("status") String status, @Param("assignee") String assignee);
}
