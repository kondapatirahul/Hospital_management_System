package com.healthcare.hospital.enterprise.quality.repository;

import com.healthcare.hospital.enterprise.quality.entity.QualityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QualityCommentRepository extends JpaRepository<QualityComment,Long> {
    List<QualityComment> findByReviewIdOrderByCreatedAtDesc(Long reviewId);
    List<QualityComment> findTop100ByAssignedToIgnoreCaseAndActionRequiredTrueOrderByFollowUpDateAsc(String username);
}
