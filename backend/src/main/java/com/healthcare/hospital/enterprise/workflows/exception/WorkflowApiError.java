package com.healthcare.hospital.enterprise.workflows.exception;
import java.time.Instant;
import java.util.List;
public record WorkflowApiError(Instant timestamp, int status, String code, String message, List<String> details) {
    public WorkflowApiError { details = details == null ? List.of() : List.copyOf(details); }
}
