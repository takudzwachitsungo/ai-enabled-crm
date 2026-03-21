package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of an SLA policy.
 */
public record SlaPolicyResponse(
        Long id,
        String name,
        String priority,
        Integer responseHours,
        String defaultAssignee,
        boolean active,
        Instant createdAt
) {
}
