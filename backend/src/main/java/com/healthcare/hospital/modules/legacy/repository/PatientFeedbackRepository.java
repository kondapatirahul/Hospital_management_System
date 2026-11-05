package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.Appointment;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.PatientFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientFeedbackRepository extends JpaRepository<PatientFeedback, Long> {
    Optional<PatientFeedback> findByPatientAndAppointment(Patient patient, Appointment appointment);
    List<PatientFeedback> findByPatientAndAppointmentIn(Patient patient, List<Appointment> appointments);
    boolean existsByAppointment(Appointment appointment);
}
