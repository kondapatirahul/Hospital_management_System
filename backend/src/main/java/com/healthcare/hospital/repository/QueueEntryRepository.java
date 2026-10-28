package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.QueueEntry;
import com.healthcare.hospital.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByQueueDateOrderByCheckInTimeAsc(LocalDate date);

    List<QueueEntry> findByQueueDateAndStatusOrderByCheckInTimeAsc(LocalDate date, QueueStatus status);

    Optional<QueueEntry> findByAppointment(Appointment appointment);

    long countByQueueDate(LocalDate date);

    long countByQueueDateAndStatus(LocalDate date, QueueStatus status);

    long countByDoctorAndQueueDateAndStatus(Doctor doctor, LocalDate date, QueueStatus status);

    @Query("SELECT q FROM QueueEntry q WHERE q.queueDate = :date AND q.doctor.id = :doctorId ORDER BY q.checkInTime ASC")
    List<QueueEntry> findByDateAndDoctor(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);
}
