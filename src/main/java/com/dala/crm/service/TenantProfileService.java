package com.dala.crm.service;

import com.dala.crm.dto.TenantProfileDto;
import java.util.List;

/**
 * Public service contract for identitytenancy module use cases.
 */
public interface TenantProfileService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<TenantProfileDto> list();
}
