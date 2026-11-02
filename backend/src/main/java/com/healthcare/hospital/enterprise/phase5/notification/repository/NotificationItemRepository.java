package com.healthcare.hospital.enterprise.phase5.notification.repository;

import com.healthcare.hospital.enterprise.phase5.notification.entity.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface NotificationItemRepository extends JpaRepository<NotificationItem, Long> {
    List<NotificationItem> findTop100ByOrderByCreatedAtDesc();
    List<NotificationItem> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<NotificationItem> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<NotificationItem> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<NotificationItem> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<NotificationItem> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
