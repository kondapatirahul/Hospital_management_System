package com.healthcare.hospital.modules.workflows.exception;
import java.util.List;
public class WorkflowValidationException extends RuntimeException {
    private final List<String> violations;
    public WorkflowValidationException(List<String> violations) {
        super("Workflow validation failed");
        this.violations = violations == null ? List.of() : List.copyOf(violations);
    }
    public List<String> getViolations() { return violations; }
}
