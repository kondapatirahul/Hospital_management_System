package com.healthcare.hospital.enterprise.phase5.systemincident.repository;

import com.healthcare.hospital.enterprise.phase5.systemincident.entity.SystemIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface SystemIncidentRepository extends JpaRepository<SystemIncident, Long> {
    List<SystemIncident> findTop100ByOrderByCreatedAtDesc();
    List<SystemIncident> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<SystemIncident> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<SystemIncident> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<SystemIncident> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<SystemIncident> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
