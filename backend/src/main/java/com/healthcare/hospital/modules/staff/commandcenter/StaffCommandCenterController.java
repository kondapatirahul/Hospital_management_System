package com.healthcare.hospital.modules.staff.commandcenter;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/command-center")
@PreAuthorize("hasAnyRole('STAFF','NURSE','ADMIN')")
public class StaffCommandCenterController {
    private final StaffCommandCenterService service;
    public StaffCommandCenterController(StaffCommandCenterService service) { this.service = service; }
    @GetMapping("/overview")
    public Map<String,Object> overview() { return service.overview(); }
    @PostMapping("/evaluate/{domain}")
    public StaffControlEvaluation evaluate(@PathVariable String domain, @RequestBody(required=false) Map<String,String> evidence) {
        return service.evaluate(domain, evidence);
    }
}
