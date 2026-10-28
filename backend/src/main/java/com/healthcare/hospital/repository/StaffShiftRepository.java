package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
    List<StaffShift> findByShiftDateOrderByStartTimeAsc(LocalDate shiftDate);
    List<StaffShift> findByStaffUsernameIgnoreCaseAndShiftDateBetweenOrderByShiftDateAscStartTimeAsc(
            String staffUsername, LocalDate start, LocalDate end);
    boolean existsByStaffUsernameIgnoreCaseAndShiftDateAndStartTimeLessThanAndEndTimeGreaterThan(
            String staffUsername, LocalDate shiftDate, java.time.LocalTime endTime, java.time.LocalTime startTime);
}
