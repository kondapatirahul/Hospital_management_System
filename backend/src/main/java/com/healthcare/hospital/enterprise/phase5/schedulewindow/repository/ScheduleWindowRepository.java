package com.healthcare.hospital.enterprise.phase5.schedulewindow.repository;

import com.healthcare.hospital.enterprise.phase5.schedulewindow.entity.ScheduleWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface ScheduleWindowRepository extends JpaRepository<ScheduleWindow, Long> {
    List<ScheduleWindow> findTop100ByOrderByCreatedAtDesc();
    List<ScheduleWindow> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<ScheduleWindow> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<ScheduleWindow> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<ScheduleWindow> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<ScheduleWindow> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
