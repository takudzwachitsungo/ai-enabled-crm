package com.dala.crm.dto;

import java.util.List;

/**
 * API representation of an installable marketplace integration.
 */
public record IntegrationMarketplaceAppDto(
        String appKey,
        String name,
        String category,
        String channelType,
        String provider,
        String version,
        String status,
        String summary,
        List<String> capabilities
) {
}
