package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.AuditEventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditController {
    private final AuditEventRepository events;
    public AdminAuditController(AuditEventRepository events) { this.events = events; }

    @GetMapping
    public String index(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                        String actor, Model model) {
        LocalDate start = from == null ? LocalDate.now().minusDays(7) : from;
        LocalDate finish = to == null ? LocalDate.now() : to;
        if (finish.isBefore(start)) {
            LocalDate swap = start; start = finish; finish = swap;
        }
        var rows = actor != null && !actor.isBlank()
                ? events.findByActorIgnoreCaseOrderByCreatedAtDesc(actor.trim())
                : events.findByCreatedAtBetweenOrderByCreatedAtDesc(start.atStartOfDay(), finish.plusDays(1).atStartOfDay());
        model.addAttribute("events", rows.stream().limit(500).toList());
        model.addAttribute("from", start);
        model.addAttribute("to", finish);
        model.addAttribute("actorFilter", actor == null ? "" : actor);
        model.addAttribute("totalEvents", rows.size());
        model.addAttribute("recentEvents", events.countByCreatedAtAfter(LocalDateTime.now().minusHours(24)));
        return "admin/audit";
    }
}
