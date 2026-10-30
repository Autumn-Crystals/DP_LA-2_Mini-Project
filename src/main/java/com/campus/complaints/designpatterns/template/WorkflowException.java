package com.campus.complaints.designpatterns.template;

/**
 * Exception thrown when a step in the {@link ComplaintWorkflow} fails validation
 * or violates lifecycle rules.
 */
public class WorkflowException extends Exception {

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
