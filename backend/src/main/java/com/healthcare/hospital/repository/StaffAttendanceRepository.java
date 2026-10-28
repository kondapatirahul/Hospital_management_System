package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {
    Optional<StaffAttendance> findByStaffUsernameIgnoreCaseAndAttendanceDate(String username, LocalDate date);
    List<StaffAttendance> findByAttendanceDateOrderByDepartmentAscStaffDisplayNameAsc(LocalDate date);
    List<StaffAttendance> findByAttendanceDateBetweenOrderByAttendanceDateDescStaffDisplayNameAsc(LocalDate start, LocalDate end);
    long countByAttendanceDateAndStatus(LocalDate date, String status);
}
