package com.healthcare.hospital.enterprise.phase5.accessreview.repository;

import com.healthcare.hospital.enterprise.phase5.accessreview.entity.AccessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;

@Repository("phase5AccessReviewRepository")
public interface AccessReviewRepository extends JpaRepository<AccessReview, Long> {
    List<AccessReview> findTop100ByOrderByCreatedAtDesc();
    List<AccessReview> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<AccessReview> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<AccessReview> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<AccessReview> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<AccessReview> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
