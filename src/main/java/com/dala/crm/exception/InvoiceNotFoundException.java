package com.dala.crm.exception;

/**
 * Thrown when an invoice cannot be found in current tenant scope.
 */
public class InvoiceNotFoundException extends NotFoundException {

    public InvoiceNotFoundException(Long id) {
        super("Invoice not found: " + id);
    }
}
