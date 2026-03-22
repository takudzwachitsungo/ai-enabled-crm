package com.dala.crm.service;

import com.dala.crm.dto.IntegrationConnectionCreateRequest;
import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.dto.IntegrationMarketplaceAppDto;
import com.dala.crm.dto.IntegrationMarketplaceInstallRequest;
import com.dala.crm.dto.IntegrationConnectionUpdateRequest;
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
     * Returns the catalog of installable marketplace integrations.
     */
    List<IntegrationMarketplaceAppDto> listMarketplaceApps();

    /**
     * Installs a marketplace integration as a tenant-scoped connection.
     */
    IntegrationConnectionDto installMarketplaceApp(IntegrationMarketplaceInstallRequest request);

    /**
     * Updates a tenant-scoped integration connection.
     */
    IntegrationConnectionDto update(Long id, IntegrationConnectionUpdateRequest request);

    /**
     * Uninstalls a tenant-scoped marketplace integration.
     */
    IntegrationConnectionDto uninstall(Long id);

    /**
     * Returns the current tenant scope list for this module.
     */
    List<IntegrationConnectionDto> list();
}
