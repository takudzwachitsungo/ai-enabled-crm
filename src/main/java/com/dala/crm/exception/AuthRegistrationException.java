package com.dala.crm.exception;

/**
 * Raised when tenant or user registration cannot be completed.
 */
public class AuthRegistrationException extends RuntimeException {

    public AuthRegistrationException(String message) {
        super(message);
    }
}
