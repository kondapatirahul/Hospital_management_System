package com.healthcare.hospital.enterprise.pharmacy.repository;

import com.healthcare.hospital.enterprise.pharmacy.entity.PharmacyRecall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for medicine recall tracking. */
public interface PharmacyRecallRepository extends JpaRepository<PharmacyRecall, Long> {
    List<PharmacyRecall> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PharmacyRecall> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<PharmacyRecall> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<PharmacyRecall> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<PharmacyRecall> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from PharmacyRecall e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from PharmacyRecall e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<PharmacyRecall> search(@Param("status") String status, @Param("assignee") String assignee);
}
