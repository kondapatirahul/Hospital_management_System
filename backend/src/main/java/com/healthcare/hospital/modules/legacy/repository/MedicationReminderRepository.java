package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.MedicationReminder;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.Prescription;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicationReminderRepository extends JpaRepository<MedicationReminder, Long> {
    boolean existsByPrescriptionAndScheduledAt(Prescription prescription, LocalDateTime scheduledAt);
    List<MedicationReminder> findTop100ByPatientOrderByScheduledAtDesc(Patient patient);
    Optional<MedicationReminder> findByIdAndPatient(Long id, Patient patient);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM MedicationReminder r WHERE r.id = :id AND r.patient = :patient")
    Optional<MedicationReminder> findByIdAndPatientForUpdate(
            @Param("id") Long id, @Param("patient") Patient patient);
}
