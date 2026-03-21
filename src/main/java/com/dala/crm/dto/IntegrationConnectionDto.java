package com.dala.crm.dto;

/**
 * API representation of an integration connection.
 */
public record IntegrationConnectionDto(
        Long id,
        String name,
        String channelType,
        String provider,
        String status,
        java.time.Instant createdAt
) {
}
