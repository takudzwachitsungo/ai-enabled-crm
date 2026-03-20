package com.dala.crm.exception;

import com.dala.crm.exception.NotFoundException;
/**
 * Raised when a lead ID is not present within the current tenant scope.
 */
public class LeadNotFoundException extends NotFoundException {

    public LeadNotFoundException(Long leadId) {
        super("Lead not found: " + leadId);
    }
}
