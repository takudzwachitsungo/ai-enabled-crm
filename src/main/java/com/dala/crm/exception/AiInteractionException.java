package com.dala.crm.exception;

/**
 * Base exception type for aiassistant module operations.
 */
public class AiInteractionException extends RuntimeException {

    public AiInteractionException(String message) {
        super(message);
    }
}
