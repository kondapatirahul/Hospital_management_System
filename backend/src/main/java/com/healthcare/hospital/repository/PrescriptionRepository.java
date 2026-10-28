package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.MedicalRecord;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);
    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(Doctor doctor);
    List<Prescription> findByAppointment(Appointment appointment);
    List<Prescription> findByMedicalRecord(MedicalRecord medicalRecord);
}
