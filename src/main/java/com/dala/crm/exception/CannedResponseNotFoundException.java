package com.dala.crm.exception;

/**
 * Thrown when a canned response cannot be found in current tenant scope.
 */
public class CannedResponseNotFoundException extends NotFoundException {

    public CannedResponseNotFoundException(Long id) {
        super("Canned response not found: " + id);
    }
}
