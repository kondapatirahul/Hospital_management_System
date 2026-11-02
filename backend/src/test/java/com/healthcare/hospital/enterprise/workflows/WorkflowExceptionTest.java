package com.healthcare.hospital.enterprise.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.enterprise.workflows.exception.*;
import java.util.List;
import org.junit.jupiter.api.Test;
class WorkflowExceptionTest {
    @Test void notFoundExceptionRetainsResourceAndIdentifier() {
        var ex=new WorkflowResourceNotFoundException("Patient",42L);
        assertEquals("Patient",ex.getResource()); assertEquals(42L,ex.getIdentifier());
    }
    @Test void validationExceptionDefensivelyCopiesViolations() {
        var violations=new java.util.ArrayList<>(List.of("missing name"));
        var ex=new WorkflowValidationException(violations); violations.clear();
        assertEquals(List.of("missing name"),ex.getViolations());
    }
    @Test void conflictExceptionPreservesReason() {
        var ex=new WorkflowConflictException("Already dispensed");
        assertEquals("Already dispensed",ex.getMessage());
    }
}
