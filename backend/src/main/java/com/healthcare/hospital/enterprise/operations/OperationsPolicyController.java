package com.healthcare.hospital.enterprise.operations;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only API for operational workflow metadata and transition preflight checks.
 * Actual records remain owned by the existing module repositories and services.
 */
@RestController
@RequestMapping("/api/enterprise/operations")
public class OperationsPolicyController {
    private final OperationsPolicyFacade facade;

    public OperationsPolicyController(OperationsPolicyFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/catalog")
    public ResponseEntity<?> catalog() {
        return ResponseEntity.ok(Map.of(
                "count", facade.catalog().size(),
                "domains", facade.catalog()
        ));
    }

    @GetMapping("/catalog/{domain}")
    public ResponseEntity<?> describe(@PathVariable String domain) {
        try {
            return ResponseEntity.ok(facade.describe(domain));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("UNKNOWN_DOMAIN", ex.getMessage()));
        }
    }

    @PostMapping("/transition-check")
    public ResponseEntity<?> checkTransition(@RequestBody TransitionCheckRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(error("INVALID_REQUEST", "Request body is required"));
        }
        try {
            return ResponseEntity.ok(facade.checkTransition(request.domain(), request.from(), request.to()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error("INVALID_REQUEST", ex.getMessage()));
        }
    }

    private Map<String, Object> error(String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", code);
        body.put("message", message == null ? "Request could not be processed" : message);
        return Map.copyOf(body);
    }

    public record TransitionCheckRequest(String domain, String from, String to) {}
}
