package com.dala.crm.service;

import com.dala.crm.dto.OpportunityCreateRequest;
import com.dala.crm.dto.OpportunityResponse;
import java.util.List;

/**
 * Public use-case contract for opportunity management.
 */
public interface OpportunityService {

    /**
     * Creates a new opportunity for the current tenant.
     */
    OpportunityResponse createOpportunity(OpportunityCreateRequest request);

    /**
     * Returns all opportunities for the current tenant.
     */
    List<OpportunityResponse> getOpportunities();

    /**
     * Returns one opportunity by ID in current tenant scope.
     */
    OpportunityResponse getOpportunity(Long id);
}
