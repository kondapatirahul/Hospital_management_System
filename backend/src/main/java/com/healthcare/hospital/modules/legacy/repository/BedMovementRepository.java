package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.BedMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BedMovementRepository extends JpaRepository<BedMovement, Long> {
    List<BedMovement> findTop100ByOrderByChangedAtDesc();
    List<BedMovement> findByBedIdOrderByChangedAtDesc(Long bedId);
}
