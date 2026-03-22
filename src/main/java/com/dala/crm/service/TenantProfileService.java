package com.dala.crm.service;

import com.dala.crm.dto.TenantProfileDto;
import com.dala.crm.dto.TenantProfileUpdateRequest;
import java.util.List;

/**
 * Public service contract for identitytenancy module use cases.
 */
public interface TenantProfileService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<TenantProfileDto> list();

    /**
     * Returns the current tenant profile including deployment settings.
     */
    TenantProfileDto getCurrent();

    /**
     * Updates the current tenant deployment settings.
     */
    TenantProfileDto updateCurrent(TenantProfileUpdateRequest request);
}
