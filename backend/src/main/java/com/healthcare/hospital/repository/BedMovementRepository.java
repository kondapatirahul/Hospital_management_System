package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.BedMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BedMovementRepository extends JpaRepository<BedMovement, Long> {
    List<BedMovement> findTop100ByOrderByChangedAtDesc();
    List<BedMovement> findByBedIdOrderByChangedAtDesc(Long bedId);
}
