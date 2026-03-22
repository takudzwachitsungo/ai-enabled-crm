package com.dala.crm.security;

/**
 * Raised when an access token is invalid or can no longer be trusted.
 */
public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String message) {
        super(message);
    }
}
