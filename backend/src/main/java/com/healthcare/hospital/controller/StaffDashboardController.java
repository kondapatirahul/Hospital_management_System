package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.QueueEntry;
import com.healthcare.hospital.enums.QueueStatus;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.QueueEntryRepository;
import com.healthcare.hospital.repository.PatientRepository;
import com.healthcare.hospital.repository.DoctorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
public class StaffDashboardController {
    private final AppointmentRepository appointmentRepository;
    private final QueueEntryRepository queueRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public StaffDashboardController(AppointmentRepository appointments, QueueEntryRepository queue,
                                    PatientRepository patients, DoctorRepository doctors) {
        this.appointmentRepository = appointments;
        this.queueRepository = queue;
        this.patientRepository = patients;
        this.doctorRepository = doctors;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        var queueEntries = queueRepository.findByQueueDateOrderByCheckInTimeAsc(today);
        model.addAttribute("todayAppointments", appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(today));
        model.addAttribute("queueEntries", queueEntries);
        model.addAttribute("waitingCount", queueEntries.stream().filter(q -> q.getStatus() == QueueStatus.WAITING).count());
        model.addAttribute("consultationCount", queueEntries.stream().filter(q -> q.getStatus() == QueueStatus.IN_CONSULTATION).count());
        model.addAttribute("completedCount", queueEntries.stream().filter(q -> q.getStatus() == QueueStatus.COMPLETED).count());
        model.addAttribute("queueStatuses", QueueStatus.values());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("doctorCount", doctorRepository.count());
        return "staff/dashboard";
    }

    @PostMapping("/queue/{id}/status")
    public String updateQueueStatus(@PathVariable Long id, @RequestParam QueueStatus status,
                                    RedirectAttributes flash) {
        QueueEntry entry = queueRepository.findById(id).orElse(null);
        if (entry == null) {
            flash.addFlashAttribute("errorMessage", "Queue entry was not found.");
            return "redirect:/staff/dashboard";
        }
        if (entry.getQueueDate() == null || !entry.getQueueDate().equals(LocalDate.now())) {
            flash.addFlashAttribute("errorMessage", "Only today's queue entries can be updated.");
            return "redirect:/staff/dashboard";
        }
        if (entry.getStatus() == QueueStatus.COMPLETED || entry.getStatus() == QueueStatus.SKIPPED) {
            flash.addFlashAttribute("errorMessage", "A completed or skipped queue entry cannot be changed.");
            return "redirect:/staff/dashboard";
        }
        if (status == QueueStatus.IN_CONSULTATION) {
            entry.setCalledTime(LocalDateTime.now());
        } else if (status == QueueStatus.COMPLETED || status == QueueStatus.SKIPPED) {
            entry.setCompletedTime(LocalDateTime.now());
        }
        entry.setStatus(status);
        queueRepository.save(entry);
        flash.addFlashAttribute("successMessage", "Queue status updated.");
        return "redirect:/staff/dashboard";
    }
}
