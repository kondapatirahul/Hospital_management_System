package com.healthcare.hospital.modules.phase5.webhookdelivery.repository;

import com.healthcare.hospital.modules.phase5.webhookdelivery.entity.WebhookDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, Long> {
    List<WebhookDelivery> findTop100ByOrderByCreatedAtDesc();
    List<WebhookDelivery> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<WebhookDelivery> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<WebhookDelivery> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<WebhookDelivery> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<WebhookDelivery> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
