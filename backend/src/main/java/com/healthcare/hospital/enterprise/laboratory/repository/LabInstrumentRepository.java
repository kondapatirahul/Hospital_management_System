package com.healthcare.hospital.enterprise.laboratory.repository;

import com.healthcare.hospital.enterprise.laboratory.entity.LabInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for laboratory instrument maintenance. */
public interface LabInstrumentRepository extends JpaRepository<LabInstrument, Long> {
    List<LabInstrument> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<LabInstrument> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<LabInstrument> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<LabInstrument> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<LabInstrument> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from LabInstrument e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from LabInstrument e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<LabInstrument> search(@Param("status") String status, @Param("assignee") String assignee);
}
