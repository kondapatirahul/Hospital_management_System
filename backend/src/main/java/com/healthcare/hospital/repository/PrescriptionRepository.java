package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.MedicalRecord;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.Prescription;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);
    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(Doctor doctor);
    List<Prescription> findByAppointment(Appointment appointment);
    List<Prescription> findByMedicalRecord(MedicalRecord medicalRecord);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prescription p WHERE p.id = :id")
    Optional<Prescription> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT p FROM Prescription p WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(p.medicationName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.patient.patientId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.doctor.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:dispenseStatus IS NULL OR :dispenseStatus = 'ALL' OR p.dispenseStatus = :dispenseStatus) " +
           "ORDER BY p.prescriptionDate DESC, p.id DESC")
    Page<Prescription> searchPrescriptions(
            @Param("query") String query,
            @Param("dispenseStatus") String dispenseStatus,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.dispenseStatus = 'PENDING' OR p.dispenseStatus = 'PARTIALLY_DISPENSED'")
    long countPendingPrescriptions();

    @Query("SELECT p FROM Prescription p WHERE p.dispenseStatus = 'PENDING' OR p.dispenseStatus = 'PARTIALLY_DISPENSED' ORDER BY p.prescriptionDate ASC, p.id ASC")
    List<Prescription> findPendingQueue();

    List<Prescription> findTop10ByOrderByCreatedAtDesc();
}
