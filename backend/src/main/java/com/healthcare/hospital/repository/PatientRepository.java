package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByUserId(Long userId);

    Optional<Patient> findByUserUsername(String username);

    boolean existsByPatientId(String patientId);

    Optional<Patient> findTopByOrderByIdDesc();

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.patientId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "p.phone LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.id DESC")
    List<Patient> searchPatients(@Param("keyword") String keyword);

    List<Patient> findAllByOrderByIdDesc();
}
