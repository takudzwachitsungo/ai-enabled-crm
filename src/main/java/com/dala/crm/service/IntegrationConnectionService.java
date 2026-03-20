package com.dala.crm.service;

import com.dala.crm.dto.IntegrationConnectionDto;
import java.util.List;

/**
 * Public service contract for integration module use cases.
 */
public interface IntegrationConnectionService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<IntegrationConnectionDto> list();
}
