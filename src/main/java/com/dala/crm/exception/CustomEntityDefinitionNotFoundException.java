package com.dala.crm.exception;

/**
 * Thrown when a custom entity definition cannot be found in current tenant scope.
 */
public class CustomEntityDefinitionNotFoundException extends NotFoundException {

    public CustomEntityDefinitionNotFoundException(Long id) {
        super("Custom entity definition not found: " + id);
    }
}
