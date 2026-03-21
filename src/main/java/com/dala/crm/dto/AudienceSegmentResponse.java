package com.dala.crm.dto;

import java.time.Instant;

/**
 * Audience segment response payload.
 */
public record AudienceSegmentResponse(
        Long id,
        String name,
        String sourceType,
        String criteria,
        Integer estimatedSize,
        boolean active,
        Instant createdAt
) {
}
