package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.LabReport;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabReportRepository extends JpaRepository<LabReport, Long> {
    List<LabReport> findByPatientOrderByRequestedDateDesc(Patient patient);
    Optional<LabReport> findByIdAndPatient(Long id, Patient patient);
}
