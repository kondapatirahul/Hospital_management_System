package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.SafetyIncident;
import com.healthcare.hospital.service.SafetyIncidentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Quality-improvement view that turns incident register data into reviewable patterns. */
@Controller
@RequestMapping("/safety/analytics")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
public class SafetyAnalyticsController {
    private final SafetyIncidentService incidents;
    public SafetyAnalyticsController(SafetyIncidentService incidents) { this.incidents = incidents; }

    @GetMapping
    public String index(Model model) {
        Map<String, Object> metrics = incidents.metrics();
        List<SafetyIncident> records = incidents.all();
        model.addAttribute("metrics", metrics);
        model.addAttribute("departmentRows", metrics.get("departments"));
        model.addAttribute("categoryRows", metrics.get("categories"));
        model.addAttribute("severityRows", metrics.get("severity"));
        model.addAttribute("recentRecords", records.stream().limit(8).toList());
        model.addAttribute("openBySeverity", openSeverity(records));
        model.addAttribute("monthRows", monthlyCounts(records));
        model.addAttribute("reportDate", LocalDate.now());
        model.addAttribute("closureRate", closureRate(records));
        model.addAttribute("averageResolutionHours", averageResolutionHours(records));
        return "safety/analytics";
    }

    private List<Map<String, Object>> openSeverity(List<SafetyIncident> records) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String severity : List.of("CRITICAL", "HIGH", "MODERATE", "LOW")) {
            long count = records.stream().filter(item -> severity.equals(item.getSeverity()))
                .filter(item -> !List.of("RESOLVED", "CLOSED").contains(item.getStatus())).count();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("label", severity); row.put("count", count); rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> monthlyCounts(List<SafetyIncident> records) {
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDate current = LocalDate.now().withDayOfMonth(1);
        for (int offset = 5; offset >= 0; offset--) {
            LocalDate month = current.minusMonths(offset);
            long count = records.stream().filter(item -> item.getReportedAt() != null)
                .filter(item -> item.getReportedAt().getYear() == month.getYear())
                .filter(item -> item.getReportedAt().getMonthValue() == month.getMonthValue()).count();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("label", month.getMonth().toString().substring(0, 3) + " " + month.getYear());
            row.put("count", count); rows.add(row);
        }
        return rows;
    }

    private int closureRate(List<SafetyIncident> records) {
        if (records.isEmpty()) return 0;
        long closed = records.stream().filter(item -> List.of("RESOLVED", "CLOSED").contains(item.getStatus())).count();
        return (int) Math.round((closed * 100.0) / records.size());
    }

    private String averageResolutionHours(List<SafetyIncident> records) {
        var durations = records.stream().filter(item -> item.getResolvedAt() != null && item.getReportedAt() != null)
            .mapToLong(item -> java.time.Duration.between(item.getReportedAt(), item.getResolvedAt()).toHours()).toArray();
        if (durations.length == 0) return "—";
        double average = java.util.Arrays.stream(durations).average().orElse(0);
        return String.format(java.util.Locale.ROOT, "%.1f", average);
    }
}
