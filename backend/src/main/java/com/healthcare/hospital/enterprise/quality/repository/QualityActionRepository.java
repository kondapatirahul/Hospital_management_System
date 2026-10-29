package com.healthcare.hospital.enterprise.quality.repository;

import com.healthcare.hospital.enterprise.quality.entity.QualityAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface QualityActionRepository extends JpaRepository<QualityAction,Long> {
    List<QualityAction> findByReviewIdOrderByCreatedAtDesc(Long reviewId);
    List<QualityAction> findByOwnerUsernameIgnoreCaseAndStatusNotInOrderByDueDateAsc(String owner, List<String> closedStates);
    List<QualityAction> findByStatusAndDueDateBeforeOrderByDueDateAsc(String status, LocalDate date);
    long countByReviewIdAndStatusNotIn(Long reviewId, List<String> closedStates);
    long countByStatus(String status);
    @Query("select a from QualityAction a where a.review.id=:reviewId and a.status in :states order by a.dueDate asc")
    List<QualityAction> findOpenActions(@Param("reviewId") Long reviewId, @Param("states") List<String> states);
}
