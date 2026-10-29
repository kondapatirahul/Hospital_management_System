package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgeryReportStatsDto;
import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.entity.SurgeryBooking;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.OperationTheatreRepository;
import com.healthcare.hospital.repository.SurgeryBookingRepository;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurgeryReportService {

    private final OperationTheatreRepository theatreRepository;
    private final SurgeryBookingRepository bookingRepository;

    public SurgeryReportService(OperationTheatreRepository theatreRepository,
                                SurgeryBookingRepository bookingRepository) {
        this.theatreRepository = theatreRepository;
        this.bookingRepository = bookingRepository;
    }

    public SurgeryReportStatsDto generateStats(LocalDate startDate, LocalDate endDate) {
        List<OperationTheatre> theatres = theatreRepository.findAll();
        List<SurgeryBooking> surgeries;

        if (startDate != null && endDate != null) {
            surgeries = bookingRepository.findByPlannedDateBetweenOrderByPlannedDateAscPlannedStartTimeAsc(startDate, endDate);
        } else {
            surgeries = bookingRepository.findAll();
        }

        SurgeryReportStatsDto stats = new SurgeryReportStatsDto();

        // Theatre counts
        stats.setTotalTheatres(theatres.size());
        stats.setAvailableTheatres(theatres.stream().filter(t -> t.getStatus() == TheatreStatus.AVAILABLE).count());
        stats.setInUseTheatres(theatres.stream().filter(t -> t.getStatus() == TheatreStatus.IN_USE).count());
        stats.setMaintenanceTheatres(theatres.stream().filter(t -> t.getStatus() == TheatreStatus.MAINTENANCE).count());

        // Surgery counts
        stats.setTotalSurgeries(surgeries.size());
        stats.setScheduledSurgeries(surgeries.stream().filter(s -> s.getStatus() == SurgeryStatus.SCHEDULED).count());
        stats.setInProgressSurgeries(surgeries.stream().filter(s -> s.getStatus() == SurgeryStatus.IN_PROGRESS).count());
        stats.setCompletedSurgeries(surgeries.stream().filter(s -> s.getStatus() == SurgeryStatus.COMPLETED).count());
        stats.setCancelledSurgeries(surgeries.stream().filter(s -> s.getStatus() == SurgeryStatus.CANCELLED).count());
        stats.setPostponedSurgeries(surgeries.stream().filter(s -> s.getStatus() == SurgeryStatus.POSTPONED).count());

        // Calculate average delay
        List<Long> delays = new ArrayList<>();
        for (SurgeryBooking s : surgeries) {
            if (s.getActualStartTime() != null && s.getPlannedDate() != null && s.getPlannedStartTime() != null) {
                LocalDateTime planned = LocalDateTime.of(s.getPlannedDate(), s.getPlannedStartTime());
                long diffMinutes = Duration.between(planned, s.getActualStartTime()).toMinutes();
                if (diffMinutes > 0) {
                    delays.add(diffMinutes);
                }
            }
        }
        double avgDelay = delays.isEmpty() ? 0.0 : delays.stream().mapToLong(Long::longValue).average().orElse(0.0);
        stats.setAverageDelayMinutes(Math.round(avgDelay * 10.0) / 10.0);

        // Utilization rate calculation
        // Operating capacity assumption: 8 operational hours per day per theatre
        long totalTheatreCount = Math.max(1, theatres.size());
        long daysCount = 30;
        if (startDate != null && endDate != null) {
            daysCount = Math.max(1, Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays() + 1);
        }
        double totalAvailableHours = totalTheatreCount * daysCount * 8.0;
        double totalBookedHours = surgeries.stream()
                .filter(s -> s.getStatus() != SurgeryStatus.CANCELLED)
                .mapToInt(s -> s.getExpectedDurationMinutes() != null ? s.getExpectedDurationMinutes() : 120)
                .sum() / 60.0;

        double utilization = totalAvailableHours > 0 ? (totalBookedHours / totalAvailableHours) * 100.0 : 0.0;
        stats.setOverallUtilizationRatePercent(Math.min(100.0, Math.round(utilization * 10.0) / 10.0));

        // Revenue calculation from linked invoices
        BigDecimal totalRevenue = surgeries.stream()
                .filter(s -> s.getInvoice() != null)
                .map(s -> s.getInvoice().getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSurgeryRevenue(totalRevenue);

        // Groupings
        Map<String, Long> categoryCounts = surgeries.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getProcedureCategory() != null ? s.getProcedureCategory() : "General",
                        Collectors.counting()
                ));
        stats.setProcedureCategoryCounts(categoryCounts);

        Map<String, Long> theatreCounts = surgeries.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getTheatre() != null ? s.getTheatre().getName() : "Unknown",
                        Collectors.counting()
                ));
        stats.setTheatreSurgeryCounts(theatreCounts);

        return stats;
    }

    public String generateCsvExport(LocalDate startDate, LocalDate endDate) {
        List<SurgeryBooking> surgeries;
        if (startDate != null && endDate != null) {
            surgeries = bookingRepository.findByPlannedDateBetweenOrderByPlannedDateAscPlannedStartTimeAsc(startDate, endDate);
        } else {
            surgeries = bookingRepository.findAll();
        }

        StringWriter sw = new StringWriter();
        sw.append("Booking Number,Patient ID,Patient Name,Procedure,Category,Priority,Theatre,Surgeon,Date,Start Time,Duration (Mins),Status,Invoice Amount\n");

        for (SurgeryBooking s : surgeries) {
            sw.append(escapeCsv(s.getBookingNumber())).append(",")
              .append(escapeCsv(s.getPatient() != null ? s.getPatient().getPatientId() : "")).append(",")
              .append(escapeCsv(s.getPatient() != null ? s.getPatient().getFullName() : "")).append(",")
              .append(escapeCsv(s.getProcedureName())).append(",")
              .append(escapeCsv(s.getProcedureCategory())).append(",")
              .append(escapeCsv(s.getPriority() != null ? s.getPriority().name() : "")).append(",")
              .append(escapeCsv(s.getTheatre() != null ? s.getTheatre().getTheatreCode() + " - " + s.getTheatre().getName() : "")).append(",")
              .append(escapeCsv(s.getLeadSurgeon() != null ? s.getLeadSurgeon().getFullName() : "")).append(",")
              .append(escapeCsv(s.getPlannedDate() != null ? s.getPlannedDate().toString() : "")).append(",")
              .append(escapeCsv(s.getPlannedStartTime() != null ? s.getPlannedStartTime().toString() : "")).append(",")
              .append(String.valueOf(s.getExpectedDurationMinutes())).append(",")
              .append(escapeCsv(s.getStatus() != null ? s.getStatus().name() : "")).append(",")
              .append(s.getInvoice() != null ? s.getInvoice().getTotalAmount().toString() : "0.00").append("\n");
        }

        return sw.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
