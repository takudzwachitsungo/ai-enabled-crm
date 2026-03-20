package com.dala.crm.exception;

/**
 * Raised when a contact ID is not present within the current tenant scope.
 */
public class ContactNotFoundException extends NotFoundException {

    public ContactNotFoundException(Long contactId) {
        super("Contact not found: " + contactId);
    }
}
