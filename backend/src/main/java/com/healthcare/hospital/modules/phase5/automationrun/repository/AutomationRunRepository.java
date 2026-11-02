package com.healthcare.hospital.modules.phase5.automationrun.repository;

import com.healthcare.hospital.modules.phase5.automationrun.entity.AutomationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface AutomationRunRepository extends JpaRepository<AutomationRun, Long> {
    List<AutomationRun> findTop100ByOrderByCreatedAtDesc();
    List<AutomationRun> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<AutomationRun> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<AutomationRun> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<AutomationRun> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<AutomationRun> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
