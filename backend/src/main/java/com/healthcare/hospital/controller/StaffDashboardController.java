package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.QueueEntryRepository;
import com.healthcare.hospital.repository.PatientRepository;
import com.healthcare.hospital.repository.DoctorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF', 'NURSE', 'ADMIN')")
public class StaffDashboardController {
    private final AppointmentRepository appointmentRepository; private final QueueEntryRepository queueRepository;
    private final PatientRepository patientRepository; private final DoctorRepository doctorRepository;
    public StaffDashboardController(AppointmentRepository appointments, QueueEntryRepository queue, PatientRepository patients, DoctorRepository doctors) {
        this.appointmentRepository=appointments; this.queueRepository=queue; this.patientRepository=patients; this.doctorRepository=doctors;
    }
    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("todayAppointments", appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate.now()));
        model.addAttribute("queueEntries", queueRepository.findAll());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("doctorCount", doctorRepository.count());
        return "staff/dashboard";
    }
}
