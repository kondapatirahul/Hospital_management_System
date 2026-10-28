package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.PatientRegistrationDto;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.service.DoctorService;
import com.healthcare.hospital.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class AuthController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    public AuthController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Authentication authentication,
                            Model model) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been safely logged out.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registrationDto")) {
            model.addAttribute("registrationDto", new PatientRegistrationDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registrationDto") PatientRegistrationDto dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            bindingResult.rejectValue("email", "error.email", "Email is required for portal account");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "error.password", "Password must be at least 6 characters");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            dto.setCreateAccount(true);
            patientService.registerPatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Your Patient ID has been issued. You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            bindingResult.reject("globalError", "An unexpected error occurred during registration. Please try again.");
            return "auth/register";
        }
    }

    // REST helper for dynamic dropdowns without full page reloads
    @GetMapping("/api/doctors-by-department")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDoctorsByDepartment(@RequestParam("departmentId") Long departmentId) {
        List<Doctor> doctors = doctorService.getDoctorsByDepartment(departmentId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Doctor d : doctors) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("name", "Dr. " + d.getFullName());
            map.put("qualification", d.getQualification());
            map.put("room", d.getRoomNumber());
            map.put("fee", d.getConsultationFee());
            map.put("availableToday", d.isAvailableToday());
            map.put("workingHours", d.getWorkingHours());
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/doctor-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getDoctorSlots(@RequestParam("doctorId") Long doctorId,
                                                       @RequestParam("date") String date) {
        List<String> standardSlots = List.of(
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM",
                "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM"
        );
        return ResponseEntity.ok(standardSlots);
    }
}
