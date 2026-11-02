package com.healthcare.hospital.modules.legacy.controller;

import com.healthcare.hospital.core.config.CustomUserDetails;
import com.healthcare.hospital.modules.legacy.entity.Appointment;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import com.healthcare.hospital.shared.enums.AppointmentStatus;
import com.healthcare.hospital.modules.legacy.repository.AppointmentRepository;
import com.healthcare.hospital.modules.legacy.service.DoctorService;
import com.healthcare.hospital.modules.doctor.dashboard.DoctorCommandCenterService;
import com.healthcare.hospital.modules.doctor.dashboard.DoctorDashboardSignalSnapshot;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor/dashboard/command-center")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorCommandCenterController {
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;
    private final DoctorCommandCenterService commandCenter;

    public DoctorCommandCenterController(DoctorService doctorService,
                                         AppointmentRepository appointmentRepository,
                                         DoctorCommandCenterService commandCenter) {
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
        this.commandCenter = commandCenter;
    }

    @GetMapping("/summary")
    public Map<String,Object> summary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) throw new IllegalStateException("Authentication required");
        Doctor doctor = doctorService.getDoctorByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
        LocalDate today = LocalDate.now();
        List<Appointment> visits = appointmentRepository
                .findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(doctor, today);
        Map<String,Double> signals = new LinkedHashMap<>();
        signals.put("UNCONFIRMED_VISITS", visits.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED).count() * 1.0);
        signals.put("CHECKIN_BACKLOG", visits.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN).count() * 1.0);
        signals.put("SLOT_UTILIZATION", visits.isEmpty() ? 0.0 : Math.min(100.0, visits.size() * 10.0));
        signals.put("CANCELLED_SLOT_REUSE", visits.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count() * 1.0);
        DoctorDashboardSignalSnapshot snapshot = new DoctorDashboardSignalSnapshot(signals, Instant.now());
        Map<String,Object> response = new LinkedHashMap<>(commandCenter.summary(snapshot));
        response.put("doctorName", doctor.getFullName());
        response.put("appointmentDate", today.toString());
        response.put("appointmentsToday", visits.size());
        response.put("dataNotice", "Only appointment metrics currently connected to live records are evaluated. Other domains show signal coverage gaps.");
        return response;
    }
}
