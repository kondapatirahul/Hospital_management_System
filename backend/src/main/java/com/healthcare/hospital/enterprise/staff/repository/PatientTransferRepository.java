package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.PatientTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for patient transfer workflow. */
public interface PatientTransferRepository extends JpaRepository<PatientTransfer, Long> {
    List<PatientTransfer> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PatientTransfer> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<PatientTransfer> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<PatientTransfer> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<PatientTransfer> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from PatientTransfer e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from PatientTransfer e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<PatientTransfer> search(@Param("status") String status, @Param("assignee") String assignee);
}
