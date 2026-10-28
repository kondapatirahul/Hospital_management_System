package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(Patient patient);

    List<Appointment> findByPatientAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(Patient patient, LocalDate date);

    List<Appointment> findByPatientAndAppointmentDateLessThanOrderByAppointmentDateDescAppointmentTimeDesc(Patient patient, LocalDate date);

    List<Appointment> findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate date);

    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    List<Appointment> findByDoctorOrderByAppointmentDateDescAppointmentTimeAsc(Doctor doctor);

    List<Appointment> findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(Doctor doctor, LocalDate date);

    List<Appointment> findByDoctorAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(Doctor doctor, LocalDate date);

    List<Appointment> findByDoctorAndAppointmentDateLessThanOrderByAppointmentDateDescAppointmentTimeDesc(Doctor doctor, LocalDate date);

    long countByAppointmentDate(LocalDate date);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    long countByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    long countByDoctorAndAppointmentDateAndStatus(Doctor doctor, LocalDate date, AppointmentStatus status);

    long countByDoctorAndStatus(Doctor doctor, AppointmentStatus status);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor = :doctor ORDER BY a.patient.fullName ASC")
    List<Patient> findDistinctPatientsByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:date IS NULL OR a.appointmentDate = :date) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:departmentId IS NULL OR a.department.id = :departmentId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.patient.patientId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.doctor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime ASC")
    List<Appointment> filterAppointments(
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status,
            @Param("doctorId") Long doctorId,
            @Param("departmentId") Long departmentId,
            @Param("keyword") String keyword
    );

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor = :doctor AND " +
           "(:date IS NULL OR a.appointmentDate = :date) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.patient.patientId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.reasonForVisit) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime ASC")
    List<Appointment> filterDoctorAppointments(
            @Param("doctor") Doctor doctor,
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status,
            @Param("keyword") String keyword
    );
}
