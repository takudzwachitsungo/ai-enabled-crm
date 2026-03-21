package com.dala.crm.exception;

/**
 * Thrown when a ticket cannot be found in current tenant scope.
 */
public class TicketNotFoundException extends NotFoundException {

    public TicketNotFoundException(Long id) {
        super("Ticket not found: " + id);
    }
}
