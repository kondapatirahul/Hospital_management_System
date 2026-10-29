package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.entity.TheatreMaintenanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreMaintenanceLogRepository extends JpaRepository<TheatreMaintenanceLog, Long> {

    List<TheatreMaintenanceLog> findByTheatreOrderByStartTimeDesc(OperationTheatre theatre);

    Page<TheatreMaintenanceLog> findAllByOrderByStartTimeDesc(Pageable pageable);
}
