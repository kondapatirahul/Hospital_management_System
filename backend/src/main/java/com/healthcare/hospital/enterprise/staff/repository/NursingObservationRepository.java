package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.NursingObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for nursing observation chart. */
public interface NursingObservationRepository extends JpaRepository<NursingObservation, Long> {
    List<NursingObservation> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<NursingObservation> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<NursingObservation> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<NursingObservation> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<NursingObservation> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from NursingObservation e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from NursingObservation e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<NursingObservation> search(@Param("status") String status, @Param("assignee") String assignee);
}
