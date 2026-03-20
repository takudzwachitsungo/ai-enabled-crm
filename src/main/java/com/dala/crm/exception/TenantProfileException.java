package com.dala.crm.exception;

/**
 * Base exception type for identitytenancy module operations.
 */
public class TenantProfileException extends RuntimeException {

    public TenantProfileException(String message) {
        super(message);
    }
}
