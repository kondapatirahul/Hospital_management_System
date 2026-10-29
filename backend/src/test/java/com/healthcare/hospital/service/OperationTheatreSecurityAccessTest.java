package com.healthcare.hospital.service;

import com.healthcare.hospital.config.CustomAuthenticationSuccessHandler;
import com.healthcare.hospital.config.CustomUserDetailsService;
import com.healthcare.hospital.config.SecurityConfig;
import com.healthcare.hospital.controller.OperationTheatreController;
import com.healthcare.hospital.dto.SurgeryReportStatsDto;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationTheatreController.class)
@Import({SecurityConfig.class, CustomAuthenticationSuccessHandler.class})
public class OperationTheatreSecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private OperationTheatreService theatreService;
    @MockBean private SurgerySchedulingService schedulingService;
    @MockBean private SurgicalTeamService teamService;
    @MockBean private SurgeryWorkflowService workflowService;
    @MockBean private SurgeryInventoryService inventoryService;
    @MockBean private SurgeryBillingService billingService;
    @MockBean private SurgeryReportService reportService;
    @MockBean private PatientRepository patientRepository;
    @MockBean private DoctorRepository doctorRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private WardBedRepository wardBedRepository;
    @MockBean private MedicineRepository medicineRepository;
    @MockBean private NotificationService notificationService;
    @MockBean private PatientService patientService;
    @MockBean private DoctorService doctorService;

    @Test
    @DisplayName("Criterion 8: Unauthenticated access to /ot/schedule redirects to /login")
    void unauthenticatedUser_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/ot/schedule"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Criterion 8: PATIENT role is forbidden (403) from accessing OT module")
    @WithMockUser(username = "patient@hospital.com", roles = {"PATIENT"})
    void patientUser_ForbiddenFromOT() throws Exception {
        mockMvc.perform(get("/ot/schedule"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Criterion 8: RECEPTIONIST role is forbidden (403) from accessing OT module")
    @WithMockUser(username = "receptionist@hospital.com", roles = {"RECEPTIONIST"})
    void receptionistUser_ForbiddenFromOT() throws Exception {
        mockMvc.perform(get("/ot/schedule"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Criterion 8: DOCTOR role is authorized to view /ot/schedule")
    @WithMockUser(username = "dr.wright@hospital.com", roles = {"DOCTOR"})
    void doctorUser_AuthorizedForSchedule() throws Exception {
        when(theatreService.getAllActiveTheatres()).thenReturn(Collections.emptyList());
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());
        when(reportService.generateStats(any(), any())).thenReturn(new SurgeryReportStatsDto());
        when(schedulingService.searchSurgeries(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/ot/schedule"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Criterion 8: NURSE role is authorized to view /ot/schedule")
    @WithMockUser(username = "nurse@hospital.com", roles = {"NURSE"})
    void nurseUser_AuthorizedForSchedule() throws Exception {
        when(theatreService.getAllActiveTheatres()).thenReturn(Collections.emptyList());
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());
        when(reportService.generateStats(any(), any())).thenReturn(new SurgeryReportStatsDto());
        when(schedulingService.searchSurgeries(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/ot/schedule"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Criterion 8: ADMIN role is authorized to view /ot/theatres")
    @WithMockUser(username = "admin@hospital.com", roles = {"ADMIN"})
    void adminUser_AuthorizedForTheatres() throws Exception {
        when(theatreService.searchTheatres(any(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/ot/theatres"))
                .andExpect(status().isOk());
    }
}
