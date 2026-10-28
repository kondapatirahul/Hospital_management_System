package com.healthcare.hospital.service;

import com.healthcare.hospital.config.CustomAuthenticationSuccessHandler;
import com.healthcare.hospital.config.CustomUserDetailsService;
import com.healthcare.hospital.config.SecurityConfig;
import com.healthcare.hospital.controller.PharmacyDashboardController;
import com.healthcare.hospital.dto.PharmacyDashboardStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PharmacyDashboardController.class)
@Import({SecurityConfig.class, CustomAuthenticationSuccessHandler.class})
public class PharmacySecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PharmacyInventoryService inventoryService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private DoctorService doctorService;

    @Test
    void unauthenticatedUser_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/pharmacy/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "patient@hospital.com", roles = {"PATIENT"})
    void patientUser_ForbiddenFromPharmacy() throws Exception {
        mockMvc.perform(get("/pharmacy/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "dr.vance@hospital.com", roles = {"DOCTOR"})
    void doctorUser_ForbiddenFromPharmacy() throws Exception {
        mockMvc.perform(get("/pharmacy/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "receptionist@hospital.com", roles = {"RECEPTIONIST"})
    void receptionistUser_ForbiddenFromPharmacy() throws Exception {
        mockMvc.perform(get("/pharmacy/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "pharmacist@hospital.com", roles = {"PHARMACIST"})
    void pharmacistUser_AccessGranted() throws Exception {
        when(inventoryService.getDashboardStats()).thenReturn(new PharmacyDashboardStatsDto());

        mockMvc.perform(get("/pharmacy/dashboard"))
                .andExpect(status().isOk());
    }
}
