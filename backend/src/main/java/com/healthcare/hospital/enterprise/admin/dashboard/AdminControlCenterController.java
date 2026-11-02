package com.healthcare.hospital.enterprise.admin.dashboard;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

/** Read-only dashboard analysis endpoints. Domain source adapters can feed normalized signals. */
@RestController
@RequestMapping("/api/admin/control-center")
@PreAuthorize("hasRole('ADMIN')")
public class AdminControlCenterController {
    private final AdminControlCenterService service;
    public AdminControlCenterController(AdminControlCenterService service) { this.service = service; }

    @GetMapping("/domains")
    public List<String> domains() { return service.supportedDomains(); }

    @PostMapping("/snapshot")
    public Map<String, Object> snapshot(@RequestBody(required = false) List<AdminDashboardSignal> signals,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        return service.dashboardSnapshot(signals == null ? List.of() : signals, asOf == null ? LocalDate.now() : asOf);
    }

    @PostMapping("/domains/{domain}")
    public Map<String, Object> evaluate(@PathVariable String domain,
            @RequestBody(required = false) List<AdminDashboardSignal> signals,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        return service.evaluateDomain(domain, signals == null ? List.of() : signals, asOf == null ? LocalDate.now() : asOf);
    }
}
