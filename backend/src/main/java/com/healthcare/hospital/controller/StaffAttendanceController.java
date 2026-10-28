package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.StaffAttendance;
import com.healthcare.hospital.repository.StaffAttendanceRepository;
import com.healthcare.hospital.service.AuditEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/staff/attendance")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
public class StaffAttendanceController {
    private final StaffAttendanceRepository attendance;
    private final AuditEventService audit;

    public StaffAttendanceController(StaffAttendanceRepository attendance, AuditEventService audit) {
        this.attendance = attendance;
        this.audit = audit;
    }

    @GetMapping
    public String index(@RequestParam(required = false) LocalDate date, Model model, Principal principal) {
        LocalDate selected = date == null ? LocalDate.now() : date;
        model.addAttribute("selectedDate", selected);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("records", attendance.findByAttendanceDateOrderByDepartmentAscStaffDisplayNameAsc(selected));
        model.addAttribute("presentCount", attendance.countByAttendanceDateAndStatus(selected, "PRESENT"));
        model.addAttribute("checkedOutCount", attendance.countByAttendanceDateAndStatus(selected, "CHECKED_OUT"));
        StaffAttendance own = principal == null ? null :
                attendance.findByStaffUsernameIgnoreCaseAndAttendanceDate(principal.getName(), LocalDate.now()).orElse(null);
        model.addAttribute("ownRecord", own);
        model.addAttribute("isToday", selected.equals(LocalDate.now()));
        return "staff/attendance";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam(defaultValue = "General") String department,
                          @RequestParam(required = false) String notes, Principal principal,
                          RedirectAttributes flash) {
        if (principal == null) return "redirect:/login";
        LocalDate today = LocalDate.now();
        var existing = attendance.findByStaffUsernameIgnoreCaseAndAttendanceDate(principal.getName(), today);
        if (existing.isPresent()) {
            flash.addFlashAttribute("errorMessage", "Attendance is already recorded for today.");
            return "redirect:/staff/attendance";
        }
        StaffAttendance record = new StaffAttendance();
        record.setStaffUsername(principal.getName());
        record.setStaffDisplayName(principal.getName());
        record.setDepartment(department == null ? "General" : department.trim().substring(0, Math.min(department.trim().length(), 100)));
        record.setAttendanceDate(today);
        record.setCheckInAt(LocalDateTime.now());
        record.setStatus("PRESENT");
        record.setNotes(notes == null ? null : notes.trim().substring(0, Math.min(notes.trim().length(), 500)));
        attendance.save(record);
        audit.record(principal.getName(), "CHECK_IN", "StaffAttendance", record.getId(), "Staff checked in");
        flash.addFlashAttribute("successMessage", "Check-in recorded.");
        return "redirect:/staff/attendance";
    }

    @PostMapping("/check-out")
    public String checkOut(Principal principal, RedirectAttributes flash) {
        if (principal == null) return "redirect:/login";
        StaffAttendance record = attendance.findByStaffUsernameIgnoreCaseAndAttendanceDate(
                principal.getName(), LocalDate.now()).orElse(null);
        if (record == null || record.getCheckInAt() == null) {
            flash.addFlashAttribute("errorMessage", "Check in before checking out.");
            return "redirect:/staff/attendance";
        }
        if (record.getCheckOutAt() != null) {
            flash.addFlashAttribute("errorMessage", "You have already checked out today.");
            return "redirect:/staff/attendance";
        }
        record.setCheckOutAt(LocalDateTime.now());
        record.setStatus("CHECKED_OUT");
        attendance.save(record);
        audit.record(principal.getName(), "CHECK_OUT", "StaffAttendance", record.getId(), "Staff checked out");
        flash.addFlashAttribute("successMessage", "Check-out recorded.");
        return "redirect:/staff/attendance";
    }
}
