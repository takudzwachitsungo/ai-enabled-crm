package com.dala.crm.exception;

/**
 * Base exception type for integration module operations.
 */
public class IntegrationConnectionException extends RuntimeException {

    public IntegrationConnectionException(String message) {
        super(message);
    }
}
