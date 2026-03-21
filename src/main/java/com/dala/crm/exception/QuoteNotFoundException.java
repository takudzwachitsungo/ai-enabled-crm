package com.dala.crm.exception;

/**
 * Thrown when a quote cannot be found in current tenant scope.
 */
public class QuoteNotFoundException extends NotFoundException {

    public QuoteNotFoundException(Long id) {
        super("Quote not found: " + id);
    }
}
