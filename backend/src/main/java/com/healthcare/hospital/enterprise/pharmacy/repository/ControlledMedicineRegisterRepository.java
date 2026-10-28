package com.healthcare.hospital.enterprise.pharmacy.repository;

import com.healthcare.hospital.enterprise.pharmacy.entity.ControlledMedicineRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for controlled medicine register. */
public interface ControlledMedicineRegisterRepository extends JpaRepository<ControlledMedicineRegister, Long> {
    List<ControlledMedicineRegister> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<ControlledMedicineRegister> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<ControlledMedicineRegister> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<ControlledMedicineRegister> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<ControlledMedicineRegister> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from ControlledMedicineRegister e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from ControlledMedicineRegister e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<ControlledMedicineRegister> search(@Param("status") String status, @Param("assignee") String assignee);
}
