package com.dala.crm.service;

import com.dala.crm.dto.LeadCreateRequest;
import com.dala.crm.dto.LeadResponse;
import java.util.List;

/**
 * Public use-case contract for lead management.
 */
public interface LeadService {

    /**
     * Creates a new lead for the current tenant.
     */
    LeadResponse createLead(LeadCreateRequest request);

    /**
     * Returns all leads for the current tenant.
     */
    List<LeadResponse> getLeads();

    /**
     * Returns one lead by ID in current tenant scope.
     */
    LeadResponse getLead(Long id);
}
