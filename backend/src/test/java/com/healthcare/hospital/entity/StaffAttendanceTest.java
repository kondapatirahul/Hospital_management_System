package com.healthcare.hospital.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/** Focused entity-level tests for attendance duration and defaults. */
class StaffAttendanceTest {
    @Test
    void newAttendanceHasNoWorkedMinutesUntilCheckIn() {
        StaffAttendance attendance = new StaffAttendance();
        assertEquals(0, attendance.getWorkedMinutes());
    }

    @Test
    void workedMinutesUsesCheckOutWhenPresent() {
        StaffAttendance attendance = new StaffAttendance();
        attendance.setAttendanceDate(LocalDate.of(2026, 10, 28));
        attendance.setCheckInAt(LocalDateTime.of(2026, 10, 28, 9, 0));
        attendance.setCheckOutAt(LocalDateTime.of(2026, 10, 28, 17, 30));
        assertEquals(510, attendance.getWorkedMinutes());
    }

    @Test
    void activeShiftReturnsNonNegativeElapsedMinutes() {
        StaffAttendance attendance = new StaffAttendance();
        attendance.setCheckInAt(LocalDateTime.now().minusMinutes(15));
        assertTrue(attendance.getWorkedMinutes() >= 15);
    }
}
