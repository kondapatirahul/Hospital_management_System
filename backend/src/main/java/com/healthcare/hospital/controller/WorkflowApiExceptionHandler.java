package com.healthcare.hospital.controller;
import com.healthcare.hospital.enterprise.workflows.exception.*;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.healthcare.hospital.enterprise.workflows")
public class WorkflowApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(WorkflowApiExceptionHandler.class);
    @ExceptionHandler(WorkflowResourceNotFoundException.class)
    public ResponseEntity<WorkflowApiError> notFound(WorkflowResourceNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND,"RESOURCE_NOT_FOUND",exception.getMessage(),List.of());
    }
    @ExceptionHandler(WorkflowValidationException.class)
    public ResponseEntity<WorkflowApiError> validation(WorkflowValidationException exception) {
        return response(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED",exception.getMessage(),exception.getViolations());
    }
    @ExceptionHandler(WorkflowConflictException.class)
    public ResponseEntity<WorkflowApiError> conflict(WorkflowConflictException exception) {
        return response(HttpStatus.CONFLICT,"WORKFLOW_CONFLICT",exception.getMessage(),List.of());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WorkflowApiError> invalidArgument(IllegalArgumentException exception) {
        return response(HttpStatus.BAD_REQUEST,"INVALID_ARGUMENT",exception.getMessage(),List.of());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WorkflowApiError> unexpected(Exception exception) {
        log.error("Unhandled clinical workflow API failure",exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","The workflow request could not be completed",List.of());
    }
    private ResponseEntity<WorkflowApiError> response(HttpStatus status,String code,String message,List<String> details) {
        WorkflowApiError body=new WorkflowApiError(Instant.now(),status.value(),code,message,details);
        return ResponseEntity.status(status).body(body);
    }
}
