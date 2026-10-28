package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.ConsultationDto;
import com.healthcare.hospital.dto.PrescriptionItemDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private QueueEntryRepository queueEntryRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConsultationService consultationService;

    private Doctor doctor;
    private Doctor otherDoctor;
    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFullName("Marcus Vance");

        otherDoctor = new Doctor();
        otherDoctor.setId(2L);
        otherDoctor.setFullName("Alexander Wright");

        patient = new Patient();
        patient.setId(10L);
        patient.setFullName("Sarah Jenkins");

        appointment = new Appointment();
        appointment.setId(100L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
    }

    @Test
    void testSaveConsultation_Success() {
        ConsultationDto dto = new ConsultationDto();
        dto.setAppointmentId(100L);
        dto.setPatientId(10L);
        dto.setDiagnosis("Hypertension");
        dto.setClinicalNotes("BP elevated at 145/95 mmHg. Advised lifestyle modifications.");
        dto.setVitalSigns("BP: 145/95 mmHg");

        PrescriptionItemDto rx = new PrescriptionItemDto("Amlodipine", "5 mg", "Once daily", "30 days", "Take in morning");
        dto.setPrescriptions(List.of(rx));

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointment(appointment)).thenReturn(Optional.empty());
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(queueEntryRepository.findByAppointment(appointment)).thenReturn(Optional.empty());

        MedicalRecord saved = consultationService.saveConsultation(doctor, dto);

        assertNotNull(saved);
        assertEquals("Hypertension", saved.getDiagnosis());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    void testSaveConsultation_UnauthorizedDoctor_ThrowsSecurityException() {
        ConsultationDto dto = new ConsultationDto();
        dto.setAppointmentId(100L);
        dto.setPatientId(10L);
        dto.setDiagnosis("Migraine");
        dto.setClinicalNotes("Patient has severe headache.");

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        // otherDoctor attempts to consult on doctor's appointment
        assertThrows(SecurityException.class, () ->
                consultationService.saveConsultation(otherDoctor, dto)
        );
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void testSaveConsultation_EmptyDiagnosis_ThrowsException() {
        ConsultationDto dto = new ConsultationDto();
        dto.setAppointmentId(100L);
        dto.setPatientId(10L);
        dto.setDiagnosis(""); // Blank diagnosis!
        dto.setClinicalNotes("Observations...");

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                consultationService.saveConsultation(doctor, dto)
        );
        assertTrue(ex.getMessage().contains("Diagnosis cannot be empty"));
    }
}
