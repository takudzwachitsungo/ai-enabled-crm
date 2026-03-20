package com.dala.crm.exception;

/**
 * Thrown when request input or state is invalid for processing.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
