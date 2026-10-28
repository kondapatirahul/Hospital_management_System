package com.healthcare.hospital.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_RECEPTIONIST")) {
                response.sendRedirect(request.getContextPath() + "/receptionist/dashboard");
                return;
            } else if (role.equals("ROLE_PATIENT")) {
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
                return;
            } else if (role.equals("ROLE_DOCTOR")) {
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
                return;
            } else if (role.equals("ROLE_PHARMACIST")) {
                response.sendRedirect(request.getContextPath() + "/pharmacy/dashboard");
                return;
            } else if (role.equals("ROLE_LAB_TECHNICIAN")) {
                response.sendRedirect(request.getContextPath() + "/laboratory/dashboard");
                return;
            } else if (role.equals("ROLE_BILLING")) {
                response.sendRedirect(request.getContextPath() + "/billing/dashboard");
                return;
            } else if (role.equals("ROLE_NURSE") || role.equals("ROLE_STAFF")) {
                response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                return;
            } else if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/");
    }
}
