package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.AppointmentRescheduleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRescheduleHistoryRepository extends JpaRepository<AppointmentRescheduleHistory, Long> {
}
