package com.healthcare.hospital.enterprise.phase5.integrationendpoint.repository;

import com.healthcare.hospital.enterprise.phase5.integrationendpoint.entity.IntegrationEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface IntegrationEndpointRepository extends JpaRepository<IntegrationEndpoint, Long> {
    List<IntegrationEndpoint> findTop100ByOrderByCreatedAtDesc();
    List<IntegrationEndpoint> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<IntegrationEndpoint> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<IntegrationEndpoint> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<IntegrationEndpoint> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<IntegrationEndpoint> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
