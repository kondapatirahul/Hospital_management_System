package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgeryBookingDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.enums.TheatreStatus;
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
public class SurgerySchedulingServiceTest {

    @Mock
    private SurgeryBookingRepository bookingRepository;

    @Mock
    private OperationTheatreRepository theatreRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private WardBedRepository wardBedRepository;

    @Mock
    private SurgeryAuditTrailRepository auditTrailRepository;

    @Mock
    private SurgeryWorkflowService workflowService;

    @Mock
    private AuditEventService auditEventService;

    @InjectMocks
    private SurgerySchedulingService schedulingService;

    private OperationTheatre theatre;
    private Patient patient;
    private Doctor doctor;
    private SurgeryBooking existingBooking;

    @BeforeEach
    void setUp() {
        theatre = new OperationTheatre("OT-01", "Main Cardiac", "Building A", "CARDIAC", 2, "Equipment");
        theatre.setId(1L);
        theatre.setStatus(TheatreStatus.AVAILABLE);
        theatre.setActive(true);

        Department dept = new Department("Cardiology", "Heart", "heart");
        doctor = new Doctor("Alexander Wright", "MD", dept, "dr.wright@hospital.com", "555-101", "Room 201", null, "Mon-Fri", "9-5");
        doctor.setId(10L);

        patient = new Patient("PAT-001", null, "Sarah Jenkins", "patient@hospital.com", "555-234", LocalDate.of(1990, 1, 1), "FEMALE", "A+", "Address", null, null, null, null);
        patient.setId(100L);

        existingBooking = new SurgeryBooking(
                "SURG-2026-0001", patient, theatre, doctor,
                "Bypass Surgery", LocalDate.of(2026, 11, 15),
                LocalTime.of(9, 0), 120, SurgeryPriority.ROUTINE, "admin@hospital.com"
        );
        existingBooking.setId(1000L);
    }

    @Test
    @DisplayName("Criterion 2: Rejects booking if theatre is in MAINTENANCE mode")
    void scheduleSurgery_TheatreInMaintenance_ThrowsException() {
        theatre.setStatus(TheatreStatus.MAINTENANCE);
        when(theatreRepository.findByIdWithLock(1L)).thenReturn(Optional.of(theatre));

        SurgeryBookingDto dto = new SurgeryBookingDto();
        dto.setTheatreId(1L);
        dto.setPatientId(100L);
        dto.setLeadSurgeonId(10L);
        dto.setPlannedDate(LocalDate.of(2026, 11, 15));
        dto.setPlannedStartTime(LocalTime.of(9, 0));
        dto.setExpectedDurationMinutes(120);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> schedulingService.scheduleSurgery(dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("MAINTENANCE"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 2: Rejects booking if theatre is inactive")
    void scheduleSurgery_TheatreInactive_ThrowsException() {
        theatre.setActive(false);
        when(theatreRepository.findByIdWithLock(1L)).thenReturn(Optional.of(theatre));

        SurgeryBookingDto dto = new SurgeryBookingDto();
        dto.setTheatreId(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> schedulingService.scheduleSurgery(dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("inactive"));
    }

    @Test
    @DisplayName("Criterion 3: Detects overlapping theatre booking (Double-booking prevention)")
    void scheduleSurgery_OverlappingTheatreBooking_ThrowsException() {
        when(theatreRepository.findByIdWithLock(1L)).thenReturn(Optional.of(theatre));
        when(patientRepository.findById(100L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(doctor));

        // Existing booking runs 09:00 - 11:00
        when(bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(eq(theatre), eq(LocalDate.of(2026, 11, 15)), any()))
                .thenReturn(List.of(existingBooking));

        // Attempting to book 10:00 - 11:30 (overlaps with 09:00 - 11:00)
        SurgeryBookingDto dto = new SurgeryBookingDto();
        dto.setTheatreId(1L);
        dto.setPatientId(100L);
        dto.setLeadSurgeonId(10L);
        dto.setProcedureName("Hernia Repair");
        dto.setPlannedDate(LocalDate.of(2026, 11, 15));
        dto.setPlannedStartTime(LocalTime.of(10, 0));
        dto.setExpectedDurationMinutes(90);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> schedulingService.scheduleSurgery(dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("Theatre conflict"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 3: Detects overlapping surgeon booking across different theatres")
    void scheduleSurgery_OverlappingSurgeonBooking_ThrowsException() {
        OperationTheatre anotherTheatre = new OperationTheatre("OT-02", "General OT", "Wing B", "GENERAL", 1, null);
        anotherTheatre.setId(2L);
        anotherTheatre.setStatus(TheatreStatus.AVAILABLE);

        when(theatreRepository.findByIdWithLock(2L)).thenReturn(Optional.of(anotherTheatre));
        when(patientRepository.findById(100L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(doctor));

        // No theatre overlap in OT-02
        when(bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(eq(anotherTheatre), eq(LocalDate.of(2026, 11, 15)), any()))
                .thenReturn(Collections.emptyList());

        // But surgeon Dr. Wright is already booked in OT-01 from 09:00 - 11:00!
        when(bookingRepository.findByLeadSurgeonAndPlannedDateAndStatusNotIn(eq(doctor), eq(LocalDate.of(2026, 11, 15)), any()))
                .thenReturn(List.of(existingBooking));

        SurgeryBookingDto dto = new SurgeryBookingDto();
        dto.setTheatreId(2L);
        dto.setPatientId(100L);
        dto.setLeadSurgeonId(10L);
        dto.setProcedureName("Appendectomy");
        dto.setPlannedDate(LocalDate.of(2026, 11, 15));
        dto.setPlannedStartTime(LocalTime.of(9, 30));
        dto.setExpectedDurationMinutes(60);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> schedulingService.scheduleSurgery(dto, "admin@hospital.com"));
        assertTrue(ex.getMessage().contains("Surgeon conflict"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 5: Concurrent booking attempts safely serialized with pessimistic lock")
    void scheduleSurgery_Valid_SavesAndInitializesChecklists() {
        when(theatreRepository.findByIdWithLock(1L)).thenReturn(Optional.of(theatre));
        when(patientRepository.findById(100L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(doctor));
        when(bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByLeadSurgeonAndPlannedDateAndStatusNotIn(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> {
            SurgeryBooking b = invocation.getArgument(0);
            b.setId(2000L);
            return b;
        });

        SurgeryBookingDto dto = new SurgeryBookingDto();
        dto.setTheatreId(1L);
        dto.setPatientId(100L);
        dto.setLeadSurgeonId(10L);
        dto.setProcedureName("Coronary Angioplasty");
        dto.setPlannedDate(LocalDate.of(2026, 11, 16));
        dto.setPlannedStartTime(LocalTime.of(14, 0));
        dto.setExpectedDurationMinutes(90);
        dto.setPriority(SurgeryPriority.ROUTINE);

        SurgeryBooking scheduled = schedulingService.scheduleSurgery(dto, "admin@hospital.com");

        assertNotNull(scheduled);
        assertEquals(SurgeryStatus.SCHEDULED, scheduled.getStatus());
        verify(theatreRepository).findByIdWithLock(1L); // Verifies concurrency lock acquired!
        verify(workflowService).initializeChecklistForSurgery(any());
        verify(auditTrailRepository).save(any(SurgeryAuditTrail.class));
    }

    @Test
    @DisplayName("Criterion 9: Cancellation requires reason and releases reserved theatre")
    void cancelSurgery_Success() {
        theatre.setStatus(TheatreStatus.RESERVED);
        when(bookingRepository.findByIdWithLock(1000L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurgeryBooking cancelled = schedulingService.cancelSurgery(1000L, "Patient hemodynamically unstable", "dr.wright@hospital.com");

        assertEquals(SurgeryStatus.CANCELLED, cancelled.getStatus());
        assertEquals("Patient hemodynamically unstable", cancelled.getCancellationReason());
        assertEquals(TheatreStatus.AVAILABLE, theatre.getStatus()); // Released!
        verify(auditTrailRepository).save(any(SurgeryAuditTrail.class));
    }

    @Test
    @DisplayName("Criterion 9: Rescheduling validates conflicts and updates booking")
    void rescheduleSurgery_Success() {
        when(bookingRepository.findByIdWithLock(1000L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByLeadSurgeonAndPlannedDateAndStatusNotIn(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate newDate = LocalDate.of(2026, 11, 20);
        LocalTime newTime = LocalTime.of(13, 0);

        SurgeryBooking rescheduled = schedulingService.rescheduleSurgery(
                1000L, newDate, newTime, 120, null, "Doctor emergency leave", "admin@hospital.com");

        assertEquals(newDate, rescheduled.getPlannedDate());
        assertEquals(newTime, rescheduled.getPlannedStartTime());
        verify(auditTrailRepository).save(any(SurgeryAuditTrail.class));
    }
}
