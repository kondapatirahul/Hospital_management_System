package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.OperationTheatreDto;
import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.entity.TheatreMaintenanceLog;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.OperationTheatreRepository;
import com.healthcare.hospital.repository.TheatreMaintenanceLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OperationTheatreService {

    private final OperationTheatreRepository theatreRepository;
    private final TheatreMaintenanceLogRepository maintenanceLogRepository;
    private final AuditEventService auditEventService;

    public OperationTheatreService(OperationTheatreRepository theatreRepository,
                                   TheatreMaintenanceLogRepository maintenanceLogRepository,
                                   AuditEventService auditEventService) {
        this.theatreRepository = theatreRepository;
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.auditEventService = auditEventService;
    }

    public List<OperationTheatre> getAllActiveTheatres() {
        return theatreRepository.findByActiveTrueOrderByTheatreCodeAsc();
    }

    public List<OperationTheatre> getAvailableTheatres() {
        return theatreRepository.findByActiveTrueAndStatusOrderByTheatreCodeAsc(TheatreStatus.AVAILABLE);
    }

    public Optional<OperationTheatre> getTheatreById(Long id) {
        return theatreRepository.findById(id);
    }

    public Optional<OperationTheatre> getTheatreByCode(String code) {
        return theatreRepository.findByTheatreCodeIgnoreCase(code);
    }

    public Page<OperationTheatre> searchTheatres(String search, TheatreStatus status, boolean activeOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("theatreCode").ascending());
        return theatreRepository.searchTheatres(search, status, activeOnly, pageable);
    }

    @Transactional
    public OperationTheatre createTheatre(OperationTheatreDto dto, String username) {
        if (theatreRepository.existsByTheatreCodeIgnoreCase(dto.getTheatreCode().trim())) {
            throw new IllegalArgumentException("Operation theatre with code '" + dto.getTheatreCode() + "' already exists.");
        }

        OperationTheatre theatre = new OperationTheatre(
                dto.getTheatreCode().trim().toUpperCase(),
                dto.getName().trim(),
                dto.getLocation().trim(),
                dto.getTheatreType() != null ? dto.getTheatreType().trim() : "GENERAL",
                dto.getCapacity(),
                dto.getEquipmentDetails()
        );
        theatre.setNotes(dto.getNotes());
        theatre.setStatus(dto.getStatus() != null ? dto.getStatus() : TheatreStatus.AVAILABLE);
        theatre.setActive(dto.isActive());

        OperationTheatre saved = theatreRepository.save(theatre);
        auditEventService.record(username, "CREATE_THEATRE", "OperationTheatre", saved.getId(),
                "Created theatre " + saved.getTheatreCode() + " (" + saved.getName() + ")");
        return saved;
    }

    @Transactional
    public OperationTheatre updateTheatre(Long id, OperationTheatreDto dto, String username) {
        OperationTheatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation theatre not found with ID: " + id));

        if (!theatre.getTheatreCode().equalsIgnoreCase(dto.getTheatreCode().trim()) &&
                theatreRepository.existsByTheatreCodeIgnoreCase(dto.getTheatreCode().trim())) {
            throw new IllegalArgumentException("Operation theatre with code '" + dto.getTheatreCode() + "' already exists.");
        }

        theatre.setTheatreCode(dto.getTheatreCode().trim().toUpperCase());
        theatre.setName(dto.getName().trim());
        theatre.setLocation(dto.getLocation().trim());
        theatre.setTheatreType(dto.getTheatreType() != null ? dto.getTheatreType().trim() : "GENERAL");
        theatre.setCapacity(dto.getCapacity());
        theatre.setEquipmentDetails(dto.getEquipmentDetails());
        if (dto.getStatus() != null) {
            theatre.setStatus(dto.getStatus());
        }
        theatre.setActive(dto.isActive());
        theatre.setNotes(dto.getNotes());

        OperationTheatre saved = theatreRepository.save(theatre);
        auditEventService.record(username, "UPDATE_THEATRE", "OperationTheatre", saved.getId(),
                "Updated theatre " + saved.getTheatreCode() + " (" + saved.getName() + ")");
        return saved;
    }

    @Transactional
    public OperationTheatre updateStatus(Long id, TheatreStatus newStatus, String username) {
        OperationTheatre theatre = theatreRepository.findByIdWithLock(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation theatre not found with ID: " + id));

        TheatreStatus oldStatus = theatre.getStatus();
        theatre.setStatus(newStatus);
        OperationTheatre saved = theatreRepository.save(theatre);

        auditEventService.record(username, "THEATRE_STATUS_CHANGE", "OperationTheatre", saved.getId(),
                "Changed status of theatre " + saved.getTheatreCode() + " from " + oldStatus + " to " + newStatus);
        return saved;
    }

    @Transactional
    public TheatreMaintenanceLog recordMaintenance(Long theatreId, String maintenanceType,
                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                    String details, String performedBy) {
        OperationTheatre theatre = theatreRepository.findByIdWithLock(theatreId)
                .orElseThrow(() -> new IllegalArgumentException("Operation theatre not found with ID: " + theatreId));

        theatre.setStatus(TheatreStatus.MAINTENANCE);
        theatreRepository.save(theatre);

        TheatreMaintenanceLog log = new TheatreMaintenanceLog(
                theatre,
                maintenanceType != null ? maintenanceType : "ROUTINE_SERVICE",
                startTime != null ? startTime : LocalDateTime.now(),
                endTime,
                performedBy,
                details
        );
        TheatreMaintenanceLog saved = maintenanceLogRepository.save(log);

        auditEventService.record(performedBy, "THEATRE_MAINTENANCE", "OperationTheatre", theatre.getId(),
                "Recorded maintenance (" + maintenanceType + ") for theatre " + theatre.getTheatreCode());
        return saved;
    }

    public List<TheatreMaintenanceLog> getMaintenanceLogsForTheatre(OperationTheatre theatre) {
        return maintenanceLogRepository.findByTheatreOrderByStartTimeDesc(theatre);
    }

    public Page<TheatreMaintenanceLog> getAllMaintenanceLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return maintenanceLogRepository.findAllByOrderByStartTimeDesc(pageable);
    }
}
