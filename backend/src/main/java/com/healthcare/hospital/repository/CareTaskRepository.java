package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.CareTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CareTaskRepository extends JpaRepository<CareTask, Long> {
    List<CareTask> findAllByOrderByDueAtAsc();
    List<CareTask> findByStatusOrderByDueAtAsc(String status);
    List<CareTask> findByAssignedToIgnoreCaseOrderByDueAtAsc(String assignedTo);
    long countByStatus(String status);
}
