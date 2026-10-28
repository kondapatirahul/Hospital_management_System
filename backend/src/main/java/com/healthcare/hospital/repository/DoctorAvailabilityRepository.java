package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorOrderByAvailableDateAscStartTimeAsc(Doctor doctor);

    List<DoctorAvailability> findByDoctorAndAvailableDateOrderByStartTimeAsc(Doctor doctor, LocalDate date);

    List<DoctorAvailability> findByDoctorAndAvailableDateGreaterThanEqualOrderByAvailableDateAscStartTimeAsc(Doctor doctor, LocalDate date);
}
