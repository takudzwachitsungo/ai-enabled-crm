package com.dala.crm.service;

import com.dala.crm.dto.SlaPolicyCreateRequest;
import com.dala.crm.dto.SlaPolicyResponse;
import java.util.List;

/**
 * Public contract for SLA policy management.
 */
public interface SlaPolicyService {

    /**
     * Creates an SLA policy for the current tenant.
     */
    SlaPolicyResponse createPolicy(SlaPolicyCreateRequest request);

    /**
     * Returns SLA policies for the current tenant.
     */
    List<SlaPolicyResponse> getPolicies();
}
