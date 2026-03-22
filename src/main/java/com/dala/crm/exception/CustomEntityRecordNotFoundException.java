package com.dala.crm.exception;

/**
 * Thrown when a custom entity record cannot be found in current tenant scope.
 */
public class CustomEntityRecordNotFoundException extends NotFoundException {

    public CustomEntityRecordNotFoundException(Long id) {
        super("Custom entity record not found: " + id);
    }
}
