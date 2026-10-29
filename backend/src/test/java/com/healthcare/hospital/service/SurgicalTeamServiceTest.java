package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgicalTeamMemberDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.RoleType;
import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurgicalTeamServiceTest {

    @Mock
    private SurgicalTeamMemberRepository teamMemberRepository;

    @Mock
    private SurgeryBookingRepository bookingRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SurgeryAuditTrailRepository auditTrailRepository;

    @InjectMocks
    private SurgicalTeamService teamService;

    private SurgeryBooking booking1;
    private SurgeryBooking booking2;
    private Doctor assistantDoc;
    private User nurseUser;

    @BeforeEach
    void setUp() {
        Department dept = new Department("General Surgery", "General", "knife");
        Doctor leadSurgeon = new Doctor("Elena Gomez", "MD", dept, "dr.gomez@hospital.com", "555-001", "Room 101", null, "Mon-Fri", "9-5");
        leadSurgeon.setId(1L);

        assistantDoc = new Doctor("Robert Chen", "MD", dept, "dr.chen@hospital.com", "555-002", "Room 102", null, "Mon-Fri", "9-5");
        assistantDoc.setId(2L);

        nurseUser = new User("nurse.mary@hospital.com", "hashed", "Mary Jane", RoleType.ROLE_NURSE);
        nurseUser.setId(20L);

        booking1 = new SurgeryBooking(
                "SURG-2026-0001", null, null, leadSurgeon,
                "Appendectomy", LocalDate.of(2026, 11, 10),
                LocalTime.of(10, 0), 120, SurgeryPriority.ROUTINE, "admin@hospital.com"
        );
        booking1.setId(100L);

        booking2 = new SurgeryBooking(
                "SURG-2026-0002", null, null, leadSurgeon,
                "Cholecystectomy", LocalDate.of(2026, 11, 10),
                LocalTime.of(11, 0), 90, SurgeryPriority.URGENT, "admin@hospital.com"
        );
        booking2.setId(200L);
    }

    @Test
    @DisplayName("Criterion 4: Prevent conflicting doctor assignment when doctor is in another overlapping surgery")
    void assignTeamMember_DoctorConflict_ThrowsException() {
        when(bookingRepository.findById(200L)).thenReturn(Optional.of(booking2));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(assistantDoc));

        // AssistantDoc is already assigned to booking1 from 10:00 - 12:00
        SurgicalTeamMember existingAssignment = new SurgicalTeamMember(booking1, assistantDoc, "ASSISTANT_SURGEON", "admin");
        when(teamMemberRepository.findConflictingDoctorAssignments(eq(assistantDoc), eq(LocalDate.of(2026, 11, 10)), any()))
                .thenReturn(List.of(existingAssignment));

        // Attempting to assign to booking2 from 11:00 - 12:30 (overlaps with 10:00 - 12:00!)
        SurgicalTeamMemberDto dto = new SurgicalTeamMemberDto();
        dto.setDoctorId(2L);
        dto.setMemberRole("ASSISTANT_SURGEON");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> teamService.assignTeamMember(200L, dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("Staff conflict"));
        verify(teamMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 4: Prevent conflicting nurse assignment when nurse is in another overlapping surgery")
    void assignTeamMember_NurseConflict_ThrowsException() {
        when(bookingRepository.findById(200L)).thenReturn(Optional.of(booking2));
        when(userRepository.findById(20L)).thenReturn(Optional.of(nurseUser));

        SurgicalTeamMember existingAssignment = new SurgicalTeamMember(booking1, nurseUser, "SCRUB_NURSE", "admin");
        when(teamMemberRepository.findConflictingUserAssignments(eq(nurseUser), eq(LocalDate.of(2026, 11, 10)), any()))
                .thenReturn(List.of(existingAssignment));

        SurgicalTeamMemberDto dto = new SurgicalTeamMemberDto();
        dto.setUserId(20L);
        dto.setMemberRole("SCRUB_NURSE");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> teamService.assignTeamMember(200L, dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("Staff conflict"));
        verify(teamMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 4: Assign staff successfully when no conflict exists")
    void assignTeamMember_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(20L)).thenReturn(Optional.of(nurseUser));
        when(teamMemberRepository.findConflictingUserAssignments(eq(nurseUser), eq(LocalDate.of(2026, 11, 10)), any()))
                .thenReturn(Collections.emptyList());
        when(teamMemberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SurgicalTeamMemberDto dto = new SurgicalTeamMemberDto();
        dto.setUserId(20L);
        dto.setMemberRole("SCRUB_NURSE");

        SurgicalTeamMember assigned = teamService.assignTeamMember(100L, dto, "admin@hospital.com");

        assertNotNull(assigned);
        assertEquals("Mary Jane", assigned.getMemberName());
        assertEquals("SCRUB_NURSE", assigned.getMemberRole());
        verify(teamMemberRepository).save(any());
        verify(auditTrailRepository).save(any());
    }

    @Test
    @DisplayName("Cannot remove LEAD_SURGEON from surgical team")
    void removeTeamMember_LeadSurgeon_ThrowsException() {
        SurgicalTeamMember leadMember = new SurgicalTeamMember(booking1, assistantDoc, "LEAD_SURGEON", "admin");
        leadMember.setId(50L);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking1));
        when(teamMemberRepository.findById(50L)).thenReturn(Optional.of(leadMember));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> teamService.removeTeamMember(100L, 50L, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("Lead surgeon cannot be removed"));
        verify(teamMemberRepository, never()).delete(any());
    }
}
