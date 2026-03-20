package com.dala.crm.exception;

/**
 * Base exception type for workflow module operations.
 */
public class WorkflowDefinitionException extends RuntimeException {

    public WorkflowDefinitionException(String message) {
        super(message);
    }
}
