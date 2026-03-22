package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update request for tenant deployment and isolation settings.
 */
public record TenantProfileUpdateRequest(
        @NotBlank @Size(max = 40) String deploymentModel,
        @Size(max = 80) String deploymentRegion,
        @Size(max = 160) String dedicatedInstanceKey
) {
}
