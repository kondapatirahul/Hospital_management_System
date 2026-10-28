package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.LabSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabSampleRepository extends JpaRepository<LabSample, Long> {
    List<LabSample> findAllByOrderByCreatedAtDesc();
    List<LabSample> findByStatusOrderByCreatedAtAsc(String status);
    Optional<LabSample> findBySampleCodeIgnoreCase(String sampleCode);
    boolean existsBySampleCodeIgnoreCase(String sampleCode);
    long countByStatus(String status);
}
