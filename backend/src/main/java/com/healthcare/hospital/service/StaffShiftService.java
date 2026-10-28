package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.StaffShift;
import com.healthcare.hospital.repository.StaffShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class StaffShiftService {
    private final StaffShiftRepository shifts;
    public StaffShiftService(StaffShiftRepository shifts) { this.shifts = shifts; }

    @Transactional
    public StaffShift schedule(String username, String displayName, String department,
                               LocalDate date, LocalTime start, LocalTime end) {
        String user = required(username, 100, "Username");
        String name = required(displayName, 120, "Staff name");
        String dept = required(department, 80, "Department");
        if (date == null || start == null || end == null) throw new IllegalArgumentException("Date and shift times are required.");
        if (!end.isAfter(start)) throw new IllegalArgumentException("Shift end time must be after start time.");
        if (date.isBefore(LocalDate.now())) throw new IllegalArgumentException("Cannot schedule a shift in the past.");
        boolean overlaps = shifts.existsByStaffUsernameIgnoreCaseAndShiftDateAndStartTimeLessThanAndEndTimeGreaterThan(
                user, date, end, start);
        if (overlaps) throw new IllegalArgumentException("This staff member already has an overlapping shift.");
        StaffShift shift = new StaffShift();
        shift.setStaffUsername(user);
        shift.setStaffDisplayName(name);
        shift.setDepartment(dept);
        shift.setShiftDate(date);
        shift.setStartTime(start);
        shift.setEndTime(end);
        shift.setStatus("SCHEDULED");
        return shifts.save(shift);
    }

    @Transactional
    public StaffShift handover(Long id, String actor, String notes) {
        StaffShift shift = shifts.findById(id).orElseThrow(() -> new IllegalArgumentException("Shift was not found."));
        if (Set.of("HANDED_OVER", "CANCELLED").contains(shift.getStatus())) {
            throw new IllegalStateException("This shift is already closed.");
        }
        if (notes == null || notes.trim().isEmpty()) throw new IllegalArgumentException("Handover notes are required.");
        if (notes.trim().length() > 2000) throw new IllegalArgumentException("Handover notes are too long.");
        shift.setHandoverNotes(notes.trim());
        shift.setHandedOverBy(required(actor, 100, "Handover user"));
        shift.setHandoverAt(LocalDateTime.now());
        shift.setStatus("HANDED_OVER");
        return shifts.save(shift);
    }

    @Transactional(readOnly = true)
    public List<StaffShift> today() { return shifts.findByShiftDateOrderByStartTimeAsc(LocalDate.now()); }

    @Transactional(readOnly = true)
    public List<StaffShift> between(LocalDate from, LocalDate to, String username) {
        if (from == null || to == null || to.isBefore(from)) throw new IllegalArgumentException("Invalid date range.");
        if (username == null || username.isBlank()) return today();
        return shifts.findByStaffUsernameIgnoreCaseAndShiftDateBetweenOrderByShiftDateAscStartTimeAsc(username, from, to);
    }

    private String required(String value, int max, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required.");
        String clean = value.trim();
        if (clean.length() > max) throw new IllegalArgumentException(label + " is too long.");
        return clean;
    }
}
