package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);
}
