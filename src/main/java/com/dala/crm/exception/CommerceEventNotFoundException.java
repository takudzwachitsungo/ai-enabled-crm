package com.dala.crm.exception;

/**
 * Thrown when a commerce event cannot be found in current tenant scope.
 */
public class CommerceEventNotFoundException extends NotFoundException {

    public CommerceEventNotFoundException(Long id) {
        super("Commerce event not found: " + id);
    }
}
