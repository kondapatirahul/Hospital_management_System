package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.DoctorAvailabilityDto;
import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.DoctorAvailability;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.DoctorAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorAvailabilityServiceTest {

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private DoctorAvailabilityService availabilityService;

    private Doctor sampleDoctor;

    @BeforeEach
    void setUp() {
        sampleDoctor = new Doctor();
        sampleDoctor.setId(10L);
        sampleDoctor.setFullName("Alexander Wright");
    }

    @Test
    void testAddSlot_Success() {
        DoctorAvailabilityDto dto = new DoctorAvailabilityDto();
        dto.setAvailableDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(12, 0));
        dto.setStatus("AVAILABLE");

        when(availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(any(), any()))
                .thenReturn(Collections.emptyList());
        when(availabilityRepository.save(any(DoctorAvailability.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DoctorAvailability saved = availabilityService.addAvailabilitySlot(sampleDoctor, dto);
        assertNotNull(saved);
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(12, 0), saved.getEndTime());
    }

    @Test
    void testAddSlot_InvalidTimeRange_ThrowsException() {
        DoctorAvailabilityDto dto = new DoctorAvailabilityDto();
        dto.setAvailableDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(15, 0));
        dto.setEndTime(LocalTime.of(10, 0)); // end before start!

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                availabilityService.addAvailabilitySlot(sampleDoctor, dto)
        );
        assertTrue(ex.getMessage().contains("End time must be after start time"));
    }

    @Test
    void testAddSlot_ConflictingSlot_ThrowsException() {
        LocalDate date = LocalDate.now().plusDays(2);
        DoctorAvailabilityDto dto = new DoctorAvailabilityDto();
        dto.setAvailableDate(date);
        dto.setStartTime(LocalTime.of(10, 0));
        dto.setEndTime(LocalTime.of(12, 0));

        DoctorAvailability existing = new DoctorAvailability(
                sampleDoctor, date, LocalTime.of(9, 0), LocalTime.of(11, 0), "AVAILABLE", "Existing"
        );

        when(availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(sampleDoctor, date))
                .thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                availabilityService.addAvailabilitySlot(sampleDoctor, dto)
        );
        assertTrue(ex.getMessage().contains("Conflicting time slot"));
    }

    @Test
    void testGetAvailableSlots_RespectsBookings() {
        LocalDate date = LocalDate.now().plusDays(1);

        Appointment booked = new Appointment();
        booked.setAppointmentSlot("09:30 AM");

        when(availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(sampleDoctor, date))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findByDoctorAndAppointmentDate(sampleDoctor, date))
                .thenReturn(List.of(booked));

        List<String> available = availabilityService.getAvailableSlotsForDoctorAndDate(sampleDoctor, date);
        assertFalse(available.contains("09:30 AM"));
        assertTrue(available.contains("09:00 AM"));
        assertTrue(available.contains("10:00 AM"));
    }
}
