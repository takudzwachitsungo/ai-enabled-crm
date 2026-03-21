package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Commerce event response payload.
 */
public record CommerceEventResponse(
        Long id,
        Long integrationConnectionId,
        String integrationName,
        String eventType,
        String sourceReference,
        String relatedEntityType,
        Long relatedEntityId,
        BigDecimal amount,
        String payload,
        Instant createdAt
) {
}
