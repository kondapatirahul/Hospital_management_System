package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.core.config.CustomAuthenticationSuccessHandler;
import com.healthcare.hospital.core.config.CustomUserDetails;
import com.healthcare.hospital.core.config.CustomUserDetailsService;
import com.healthcare.hospital.core.config.SecurityConfig;
import com.healthcare.hospital.modules.legacy.controller.DoctorDashboardController;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import com.healthcare.hospital.modules.legacy.entity.User;
import com.healthcare.hospital.shared.enums.RoleType;
import com.healthcare.hospital.modules.legacy.repository.AppointmentRepository;
import com.healthcare.hospital.modules.legacy.repository.PatientRepository;
import com.healthcare.hospital.modules.legacy.repository.QueueEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorDashboardController.class)
@Import({SecurityConfig.class, CustomAuthenticationSuccessHandler.class})
public class DoctorSecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private AppointmentRepository appointmentRepository;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private DoctorAvailabilityService availabilityService;

    @MockBean
    private MedicalService medicalService;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private QueueEntryRepository queueEntryRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PatientService patientService;

    private Doctor doctor;

    @BeforeEach
    void setup() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFullName("Marcus Vance");
        doctor.setEmail("dr.vance@hospital.com");
    }

    @Test
    void testUnauthenticated_RedirectedToLogin() throws Exception {
        mockMvc.perform(get("/doctor/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "patient@hospital.com", roles = {"PATIENT"})
    void testPatientRole_CannotAccessDoctorDashboard() throws Exception {
        mockMvc.perform(get("/doctor/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "receptionist@hospital.com", roles = {"RECEPTIONIST"})
    void testReceptionistRole_CannotAccessDoctorDashboard() throws Exception {
        mockMvc.perform(get("/doctor/dashboard"))
                .andExpect(status().isForbidden());
    }
}
