package com.healthcare.hospital.controller;

import com.healthcare.hospital.service.StaffShiftService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/staff/shifts")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
public class StaffShiftController {
    private final StaffShiftService shifts;
    public StaffShiftController(StaffShiftService shifts) { this.shifts = shifts; }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("shifts", shifts.today());
        model.addAttribute("today", LocalDate.now());
        return "staff/shifts";
    }

    @PostMapping
    public String schedule(@RequestParam String staffUsername, @RequestParam String staffDisplayName,
                           @RequestParam String department, @RequestParam LocalDate shiftDate,
                           @RequestParam LocalTime startTime, @RequestParam LocalTime endTime,
                           RedirectAttributes flash) {
        try {
            shifts.schedule(staffUsername, staffDisplayName, department, shiftDate, startTime, endTime);
            flash.addFlashAttribute("successMessage", "Staff shift scheduled.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/shifts";
    }

    @PostMapping("/{id}/handover")
    public String handover(@PathVariable Long id, @RequestParam String handoverNotes,
                           Principal principal, RedirectAttributes flash) {
        try {
            shifts.handover(id, principal == null ? "staff-user" : principal.getName(), handoverNotes);
            flash.addFlashAttribute("successMessage", "Shift handover saved.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/shifts";
    }
}
