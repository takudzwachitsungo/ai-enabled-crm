package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of an audit log record.
 */
public record AuditLogResponse(
        Long id,
        String actor,
        String action,
        String entityType,
        Long entityId,
        String summary,
        Instant createdAt
) {
}
