package com.dala.crm.exception;

/**
 * Raised when an audience segment cannot be found for the current tenant.
 */
public class AudienceSegmentNotFoundException extends NotFoundException {

    public AudienceSegmentNotFoundException(Long id) {
        super("Audience segment not found: " + id);
    }
}
