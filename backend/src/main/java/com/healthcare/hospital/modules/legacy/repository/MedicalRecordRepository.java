package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.Appointment;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import com.healthcare.hospital.modules.legacy.entity.MedicalRecord;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByVisitDateDesc(Patient patient);
    List<MedicalRecord> findByDoctorOrderByVisitDateDesc(Doctor doctor);
    Optional<MedicalRecord> findByAppointment(Appointment appointment);
    List<MedicalRecord> findByPatientAndDoctorOrderByVisitDateDesc(Patient patient, Doctor doctor);
}
