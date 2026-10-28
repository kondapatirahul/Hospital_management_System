package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.LabReport;
import com.healthcare.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabReportRepository extends JpaRepository<LabReport, Long> {
    List<LabReport> findByPatientOrderByRequestedDateDesc(Patient patient);
}
