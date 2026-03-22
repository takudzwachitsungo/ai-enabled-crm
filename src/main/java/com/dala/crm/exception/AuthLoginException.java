package com.dala.crm.exception;

/**
 * Raised when login credentials are invalid for a tenant workspace.
 */
public class AuthLoginException extends RuntimeException {

    public AuthLoginException(String message) {
        super(message);
    }
}
