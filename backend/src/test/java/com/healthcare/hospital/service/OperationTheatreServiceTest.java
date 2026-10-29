package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.OperationTheatreDto;
import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.entity.TheatreMaintenanceLog;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.OperationTheatreRepository;
import com.healthcare.hospital.repository.TheatreMaintenanceLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationTheatreServiceTest {

    @Mock
    private OperationTheatreRepository theatreRepository;

    @Mock
    private TheatreMaintenanceLogRepository maintenanceLogRepository;

    @Mock
    private AuditEventService auditEventService;

    @InjectMocks
    private OperationTheatreService theatreService;

    private OperationTheatre sampleTheatre;

    @BeforeEach
    void setUp() {
        sampleTheatre = new OperationTheatre("OT-01", "Main Cardiac OT", "Building A, 3rd Floor", "CARDIAC", 2, "Bypass machine");
        sampleTheatre.setId(1L);
    }

    @Test
    @DisplayName("Criterion 1: Create theatre successfully when code is unique")
    void createTheatre_Success() {
        OperationTheatreDto dto = new OperationTheatreDto();
        dto.setTheatreCode("OT-02");
        dto.setName("General Surgery Suite");
        dto.setLocation("Wing B, 2nd Floor");
        dto.setTheatreType("GENERAL");
        dto.setCapacity(2);
        dto.setEquipmentDetails("Laparoscopy tower");

        when(theatreRepository.existsByTheatreCodeIgnoreCase("OT-02")).thenReturn(false);
        when(theatreRepository.save(any(OperationTheatre.class))).thenAnswer(invocation -> {
            OperationTheatre saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        OperationTheatre created = theatreService.createTheatre(dto, "admin@hospital.com");

        assertNotNull(created);
        assertEquals("OT-02", created.getTheatreCode());
        assertEquals("General Surgery Suite", created.getName());
        assertEquals(TheatreStatus.AVAILABLE, created.getStatus());
        verify(theatreRepository, times(1)).save(any(OperationTheatre.class));
        verify(auditEventService, times(1)).record(eq("admin@hospital.com"), eq("CREATE_THEATRE"), eq("OperationTheatre"), any(), any());
    }

    @Test
    @DisplayName("Criterion 1: Create theatre throws exception when code already exists")
    void createTheatre_DuplicateCode_ThrowsException() {
        OperationTheatreDto dto = new OperationTheatreDto();
        dto.setTheatreCode("OT-01");
        dto.setName("Duplicate Room");

        when(theatreRepository.existsByTheatreCodeIgnoreCase("OT-01")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> theatreService.createTheatre(dto, "admin@hospital.com"));
        verify(theatreRepository, never()).save(any(OperationTheatre.class));
    }

    @Test
    @DisplayName("Criterion 1: Update theatre updates fields and logs audit event")
    void updateTheatre_Success() {
        OperationTheatreDto dto = new OperationTheatreDto();
        dto.setTheatreCode("OT-01");
        dto.setName("Renovated Cardiac OT");
        dto.setLocation("Building A, 3rd Floor, West");
        dto.setTheatreType("CARDIAC");
        dto.setCapacity(3);
        dto.setEquipmentDetails("Upgraded heart-lung machine");
        dto.setActive(true);

        when(theatreRepository.findById(1L)).thenReturn(Optional.of(sampleTheatre));
        when(theatreRepository.save(any(OperationTheatre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OperationTheatre updated = theatreService.updateTheatre(1L, dto, "admin@hospital.com");

        assertEquals("Renovated Cardiac OT", updated.getName());
        assertEquals(3, updated.getCapacity());
        verify(theatreRepository).save(sampleTheatre);
        verify(auditEventService).record(eq("admin@hospital.com"), eq("UPDATE_THEATRE"), eq("OperationTheatre"), eq(1L), any());
    }

    @Test
    @DisplayName("Record maintenance transitions theatre status to MAINTENANCE and creates log")
    void recordMaintenance_Success() {
        when(theatreRepository.findByIdWithLock(1L)).thenReturn(Optional.of(sampleTheatre));
        when(maintenanceLogRepository.save(any(TheatreMaintenanceLog.class))).thenAnswer(invocation -> {
            TheatreMaintenanceLog log = invocation.getArgument(0);
            log.setId(10L);
            return log;
        });

        LocalDateTime now = LocalDateTime.now();
        TheatreMaintenanceLog log = theatreService.recordMaintenance(
                1L, "ROUTINE_SERVICE", now, now.plusHours(4), "Bi-monthly filter change", "engineer@hospital.com");

        assertNotNull(log);
        assertEquals(TheatreStatus.MAINTENANCE, sampleTheatre.getStatus());
        verify(theatreRepository).save(sampleTheatre);
        verify(maintenanceLogRepository).save(any(TheatreMaintenanceLog.class));
    }

    @Test
    @DisplayName("Search theatres with filters and pagination")
    void searchTheatres_Pagination() {
        Page<OperationTheatre> page = new PageImpl<>(List.of(sampleTheatre));
        when(theatreRepository.searchTheatres(eq("Cardiac"), eq(TheatreStatus.AVAILABLE), eq(true), any(Pageable.class)))
                .thenReturn(page);

        Page<OperationTheatre> result = theatreService.searchTheatres("Cardiac", TheatreStatus.AVAILABLE, true, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("OT-01", result.getContent().get(0).getTheatreCode());
    }
}
