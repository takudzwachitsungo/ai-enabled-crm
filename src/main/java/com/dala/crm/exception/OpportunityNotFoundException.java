package com.dala.crm.exception;

/**
 * Raised when an opportunity ID is not present within the current tenant scope.
 */
public class OpportunityNotFoundException extends NotFoundException {

    public OpportunityNotFoundException(Long opportunityId) {
        super("Opportunity not found: " + opportunityId);
    }
}
