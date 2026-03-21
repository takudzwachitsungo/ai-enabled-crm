package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a CRM activity.
 */
public record ActivityResponse(
        Long id,
        String type,
        String subject,
        String relatedEntityType,
        Long relatedEntityId,
        String details,
        Instant createdAt
) {
}
