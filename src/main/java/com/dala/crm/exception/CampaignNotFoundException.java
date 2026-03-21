package com.dala.crm.exception;

/**
 * Raised when a campaign cannot be found for the current tenant.
 */
public class CampaignNotFoundException extends NotFoundException {

    public CampaignNotFoundException(Long id) {
        super("Campaign not found: " + id);
    }
}
