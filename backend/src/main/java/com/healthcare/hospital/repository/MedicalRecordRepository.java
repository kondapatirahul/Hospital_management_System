package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.MedicalRecord;
import com.healthcare.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByVisitDateDesc(Patient patient);
}
