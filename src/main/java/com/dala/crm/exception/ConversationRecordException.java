package com.dala.crm.exception;

/**
 * Base exception type for communication module operations.
 */
public class ConversationRecordException extends RuntimeException {

    public ConversationRecordException(String message) {
        super(message);
    }
}
