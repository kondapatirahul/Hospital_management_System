package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.AccessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for periodic access review. */
public interface AccessReviewRepository extends JpaRepository<AccessReview, Long> {
    List<AccessReview> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<AccessReview> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<AccessReview> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<AccessReview> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<AccessReview> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from AccessReview e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from AccessReview e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<AccessReview> search(@Param("status") String status, @Param("assignee") String assignee);
}
