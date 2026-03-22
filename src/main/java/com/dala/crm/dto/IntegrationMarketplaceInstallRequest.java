package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for installing a marketplace integration.
 */
public record IntegrationMarketplaceInstallRequest(
        @NotBlank @Size(max = 80) String appKey,
        @Size(max = 160) String connectionName
) {
}
