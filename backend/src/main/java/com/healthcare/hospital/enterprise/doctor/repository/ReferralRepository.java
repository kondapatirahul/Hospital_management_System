package com.healthcare.hospital.enterprise.doctor.repository;

import com.healthcare.hospital.enterprise.doctor.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for clinical referral tracking. */
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<Referral> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<Referral> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<Referral> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<Referral> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from Referral e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from Referral e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<Referral> search(@Param("status") String status, @Param("assignee") String assignee);
}
