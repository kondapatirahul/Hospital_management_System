package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.Department;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final LabReportRepository labReportRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminDashboardController(UserRepository users, PatientRepository patients, DoctorRepository doctors,
            AppointmentRepository appointments, DepartmentRepository departments, LabReportRepository labs,
            InvoiceRepository invoices) {
        this.userRepository = users;
        this.patientRepository = patients;
        this.doctorRepository = doctors;
        this.appointmentRepository = appointments;
        this.departmentRepository = departments;
        this.labReportRepository = labs;
        this.invoiceRepository = invoices;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("enabledUserCount", userRepository.findAll().stream().filter(User::isEnabled).count());
        model.addAttribute("disabledUserCount", userRepository.findAll().stream().filter(user -> !user.isEnabled()).count());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("doctorCount", doctorRepository.count());
        model.addAttribute("appointmentCount", appointmentRepository.count());
        model.addAttribute("departmentCount", departmentRepository.count());
        model.addAttribute("labReportCount", labReportRepository.count());
        model.addAttribute("invoiceCount", invoiceRepository.count());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            flash.addFlashAttribute("errorMessage", "User account was not found.");
        } else if (authentication != null && user.getUsername().equalsIgnoreCase(authentication.getName())) {
            flash.addFlashAttribute("errorMessage", "You cannot disable your own signed-in account.");
        } else {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            flash.addFlashAttribute("successMessage",
                    user.isEnabled() ? "User account enabled." : "User account disabled.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/departments")
    public String createDepartment(@RequestParam String name,
                                  @RequestParam(required = false) String description,
                                  RedirectAttributes flash) {
        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isBlank()) {
            flash.addFlashAttribute("errorMessage", "Department name is required.");
            return "redirect:/admin/dashboard";
        }
        if (cleanName.length() > 100) {
            flash.addFlashAttribute("errorMessage", "Department name must be 100 characters or fewer.");
            return "redirect:/admin/dashboard";
        }
        if (departmentRepository.findByName(cleanName).isPresent()) {
            flash.addFlashAttribute("errorMessage", "A department with that name already exists.");
            return "redirect:/admin/dashboard";
        }
        String cleanDescription = description == null ? "" : description.trim();
        departmentRepository.save(new Department(cleanName,
                cleanDescription.isBlank() ? null : cleanDescription, "hospital"));
        flash.addFlashAttribute("successMessage", "Department created successfully.");
        return "redirect:/admin/dashboard";
    }
}
