package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final UserRepository userRepository; private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository; private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository; private final LabReportRepository labReportRepository;
    private final InvoiceRepository invoiceRepository;
    public AdminDashboardController(UserRepository users, PatientRepository patients, DoctorRepository doctors,
            AppointmentRepository appointments, DepartmentRepository departments, LabReportRepository labs, InvoiceRepository invoices) {
        this.userRepository=users; this.patientRepository=patients; this.doctorRepository=doctors;
        this.appointmentRepository=appointments; this.departmentRepository=departments; this.labReportRepository=labs; this.invoiceRepository=invoices;
    }
    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("doctorCount", doctorRepository.count());
        model.addAttribute("appointmentCount", appointmentRepository.count());
        model.addAttribute("departmentCount", departmentRepository.count());
        model.addAttribute("labReportCount", labReportRepository.count());
        model.addAttribute("invoiceCount", invoiceRepository.count());
        model.addAttribute("users", userRepository.findAll());
        return "admin/dashboard";
    }
}
