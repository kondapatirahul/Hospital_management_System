package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.WardRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for ward round observations. */
public interface WardRoundRepository extends JpaRepository<WardRound, Long> {
    List<WardRound> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<WardRound> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<WardRound> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<WardRound> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<WardRound> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from WardRound e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from WardRound e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<WardRound> search(@Param("status") String status, @Param("assignee") String assignee);
}
