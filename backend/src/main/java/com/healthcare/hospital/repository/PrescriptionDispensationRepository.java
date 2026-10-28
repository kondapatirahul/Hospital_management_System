package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.Prescription;
import com.healthcare.hospital.entity.PrescriptionDispensation;
import com.healthcare.hospital.enums.DispensationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionDispensationRepository extends JpaRepository<PrescriptionDispensation, Long> {

    Optional<PrescriptionDispensation> findByDispensationNumberIgnoreCase(String dispensationNumber);

    List<PrescriptionDispensation> findByPrescription(Prescription prescription);

    List<PrescriptionDispensation> findByPatientOrderByDispensationDateDesc(Patient patient);

    List<PrescriptionDispensation> findTop10ByOrderByDispensationDateDesc();

    @Query("SELECT d FROM PrescriptionDispensation d WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(d.dispensationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(d.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(d.patient.patientId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(d.doctor.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:startDate IS NULL OR d.dispensationDate >= :startDate) AND " +
           "(:endDate IS NULL OR d.dispensationDate <= :endDate) " +
           "ORDER BY d.dispensationDate DESC")
    Page<PrescriptionDispensation> searchDispensations(
            @Param("query") String query,
            @Param("status") DispensationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(d) FROM PrescriptionDispensation d WHERE d.status = com.healthcare.hospital.enums.DispensationStatus.COMPLETED")
    long countCompletedDispensations();

    @Query("SELECT d FROM PrescriptionDispensation d WHERE " +
           "(:startDate IS NULL OR d.dispensationDate >= :startDate) AND " +
           "(:endDate IS NULL OR d.dispensationDate <= :endDate) " +
           "ORDER BY d.dispensationDate DESC")
    List<PrescriptionDispensation> findDispensationsReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
