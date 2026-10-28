package com.healthcare.hospital.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_RECEPTIONIST")) {
                    return "redirect:/receptionist/dashboard";
                } else if (authority.getAuthority().equals("ROLE_PATIENT")) {
                    return "redirect:/patient/dashboard";
                } else if (authority.getAuthority().equals("ROLE_DOCTOR")) {
                    return "redirect:/doctor/dashboard";
                } else if (authority.getAuthority().equals("ROLE_PHARMACIST")) {
                    return "redirect:/pharmacy/dashboard";
                } else if (authority.getAuthority().equals("ROLE_LAB_TECHNICIAN")) {
                    return "redirect:/laboratory/dashboard";
                } else if (authority.getAuthority().equals("ROLE_BILLING")) {
                    return "redirect:/billing/dashboard";
                } else if (authority.getAuthority().equals("ROLE_NURSE") || authority.getAuthority().equals("ROLE_STAFF")) {
                    return "redirect:/staff/dashboard";
                } else if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                }
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
