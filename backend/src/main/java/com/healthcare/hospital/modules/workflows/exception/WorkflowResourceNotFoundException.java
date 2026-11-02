package com.healthcare.hospital.modules.workflows.exception;
public class WorkflowResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final Object identifier;
    public WorkflowResourceNotFoundException(String resource, Object identifier) {
        super(resource + " was not found for identifier: " + identifier);
        this.resource = resource; this.identifier = identifier;
    }
    public String getResource() { return resource; }
    public Object getIdentifier() { return identifier; }
}
