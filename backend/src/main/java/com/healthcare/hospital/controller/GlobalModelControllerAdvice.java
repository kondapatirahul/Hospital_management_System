package com.healthcare.hospital.controller;

import com.healthcare.hospital.config.CustomUserDetails;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.service.DoctorService;
import com.healthcare.hospital.service.NotificationService;
import com.healthcare.hospital.service.PatientService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.Optional;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final NotificationService notificationService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public GlobalModelControllerAdvice(NotificationService notificationService,
                                       PatientService patientService,
                                       DoctorService doctorService) {
        this.notificationService = notificationService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Authentication authentication, Model model) {
        model.addAttribute("currentDate", LocalDate.now());

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentRole", currentUser.getRole().name());

            long unreadNotifications = notificationService.getUnreadCount(currentUser);
            model.addAttribute("unreadNotificationCount", unreadNotifications);

            if (currentUser.getRole().name().equals("ROLE_PATIENT")) {
                Optional<Patient> currentPatient = patientService.getPatientByUsername(currentUser.getUsername());
                currentPatient.ifPresent(p -> model.addAttribute("currentPatient", p));
            } else if (currentUser.getRole().name().equals("ROLE_DOCTOR")) {
                Optional<Doctor> currentDoctor = doctorService.getDoctorByUsername(currentUser.getUsername());
                currentDoctor.ifPresent(d -> model.addAttribute("currentDoctor", d));
            }
        }
    }
}
