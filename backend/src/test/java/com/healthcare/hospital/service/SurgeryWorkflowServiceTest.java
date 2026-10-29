package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.ChecklistItemUpdateDto;
import com.healthcare.hospital.dto.SurgeryStatusUpdateDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.ChecklistPhase;
import com.healthcare.hospital.enums.ChecklistStatus;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurgeryWorkflowServiceTest {

    @Mock
    private SurgeryBookingRepository bookingRepository;

    @Mock
    private SurgeryChecklistItemRepository checklistRepository;

    @Mock
    private SurgeryAuditTrailRepository auditTrailRepository;

    @Mock
    private OperationTheatreRepository theatreRepository;

    @Mock
    private AuditEventService auditEventService;

    @InjectMocks
    private SurgeryWorkflowService workflowService;

    private SurgeryBooking booking;
    private OperationTheatre theatre;

    @BeforeEach
    void setUp() {
        theatre = new OperationTheatre("OT-01", "Main OT", "Wing A", "GENERAL", 1, null);
        theatre.setId(1L);
        theatre.setStatus(TheatreStatus.AVAILABLE);

        booking = new SurgeryBooking(
                "SURG-2026-0001", null, theatre, null,
                "Hernia Repair", LocalDate.of(2026, 11, 10),
                LocalTime.of(9, 0), 90, SurgeryPriority.ROUTINE, "admin@hospital.com"
        );
        booking.setId(10L);
        booking.setStatus(SurgeryStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Criterion 6: Rejects invalid workflow transition (SCHEDULED directly to COMPLETED)")
    void updateStatus_InvalidTransition_ThrowsException() {
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));

        SurgeryStatusUpdateDto dto = new SurgeryStatusUpdateDto();
        dto.setTargetStatus(SurgeryStatus.COMPLETED);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> workflowService.updateSurgeryStatus(10L, dto, "dr.surgeon@hospital.com"));
        assertTrue(ex.getMessage().contains("Invalid workflow transition"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 6: Rejects transition from CANCELLED to IN_PROGRESS")
    void updateStatus_CancelledToInProgress_ThrowsException() {
        booking.setStatus(SurgeryStatus.CANCELLED);
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));

        SurgeryStatusUpdateDto dto = new SurgeryStatusUpdateDto();
        dto.setTargetStatus(SurgeryStatus.IN_PROGRESS);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> workflowService.updateSurgeryStatus(10L, dto, "dr.surgeon@hospital.com"));
        assertTrue(ex.getMessage().contains("Invalid workflow transition"));
    }

    @Test
    @DisplayName("Criterion 7: Blocks transition to IN_PROGRESS when mandatory preoperative checks are incomplete")
    void updateStatus_MissingMandatoryPreopChecks_ThrowsException() {
        booking.setStatus(SurgeryStatus.PREPARATION);
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));

        // There are still 2 incomplete mandatory preop checks
        when(checklistRepository.countBySurgeryBookingAndPhaseAndIsMandatoryTrueAndStatusNot(
                eq(booking), eq(ChecklistPhase.PREOPERATIVE), eq(ChecklistStatus.COMPLETED)))
                .thenReturn(2L);

        SurgeryStatusUpdateDto dto = new SurgeryStatusUpdateDto();
        dto.setTargetStatus(SurgeryStatus.IN_PROGRESS);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> workflowService.updateSurgeryStatus(10L, dto, "dr.surgeon@hospital.com"));
        assertTrue(ex.getMessage().contains("Mandatory Preoperative checklist items are incomplete"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 7: Allows transition to IN_PROGRESS when all mandatory preoperative checks are COMPLETED")
    void updateStatus_MandatoryPreopChecksComplete_TransitionsToInProgress() {
        booking.setStatus(SurgeryStatus.PREPARATION);
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));

        // 0 pending mandatory checks
        when(checklistRepository.countBySurgeryBookingAndPhaseAndIsMandatoryTrueAndStatusNot(
                eq(booking), eq(ChecklistPhase.PREOPERATIVE), eq(ChecklistStatus.COMPLETED)))
                .thenReturn(0L);
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurgeryStatusUpdateDto dto = new SurgeryStatusUpdateDto();
        dto.setTargetStatus(SurgeryStatus.IN_PROGRESS);

        SurgeryBooking updated = workflowService.updateSurgeryStatus(10L, dto, "dr.surgeon@hospital.com");

        assertEquals(SurgeryStatus.IN_PROGRESS, updated.getStatus());
        assertNotNull(updated.getActualStartTime());
        assertEquals(TheatreStatus.IN_USE, theatre.getStatus()); // Room set to IN_USE!
        verify(theatreRepository).save(theatre);
        verify(auditTrailRepository).save(any(SurgeryAuditTrail.class));
    }

    @Test
    @DisplayName("Document operative notes updates surgery booking and logs audit")
    void saveOperativeNotes_Success() {
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurgeryBooking updated = workflowService.saveOperativeNotes(
                10L, "Mesh hernioplasty performed uneventfully.", "Spinal anesthesia", "Right inguinal hernia", "dr.wright@hospital.com");

        assertEquals("Mesh hernioplasty performed uneventfully.", updated.getOperativeNotes());
        assertEquals("Spinal anesthesia", updated.getAnesthesiaNotes());
        verify(auditTrailRepository).save(any());
    }

    @Test
    @DisplayName("Postoperative handover requires recovery destination")
    void recordHandover_MissingDestination_ThrowsException() {
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> workflowService.recordHandover(10L, "   ", "Patient stable", "nurse@hospital.com"));
    }

    @Test
    @DisplayName("Postoperative handover sets destination and logs audit")
    void recordHandover_Success() {
        when(bookingRepository.findByIdWithLock(10L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(SurgeryBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurgeryBooking updated = workflowService.recordHandover(10L, "PACU Bed 04", "Vitals stable, Aldrete score 9", "nurse@hospital.com");

        assertEquals("PACU Bed 04", updated.getRecoveryDestination());
        verify(auditTrailRepository).save(any());
    }
}
