package com.dala.crm.dto;

/**
 * Tenant profile settings including isolation and deployment posture.
 */
public record TenantProfileDto(
        Long id,
        String tenantId,
        String name,
        String deploymentModel,
        String deploymentStatus,
        String deploymentRegion,
        String dedicatedInstanceKey
) {
}
