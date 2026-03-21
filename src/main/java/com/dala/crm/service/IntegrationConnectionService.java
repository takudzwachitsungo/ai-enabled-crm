package com.dala.crm.service;

import com.dala.crm.dto.IntegrationConnectionCreateRequest;
import com.dala.crm.dto.IntegrationConnectionDto;
import java.util.List;

/**
 * Public service contract for integration module use cases.
 */
public interface IntegrationConnectionService {

    /**
     * Creates an integration connection for the current tenant.
     */
    IntegrationConnectionDto create(IntegrationConnectionCreateRequest request);

    /**
     * Returns the current tenant scope list for this module.
     */
    List<IntegrationConnectionDto> list();
}
