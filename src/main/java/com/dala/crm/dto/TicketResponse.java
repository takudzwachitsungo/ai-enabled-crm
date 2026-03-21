package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a service ticket.
 */
public record TicketResponse(
        Long id,
        String title,
        String description,
        String priority,
        String status,
        String assignee,
        String sourceChannel,
        String relatedEntityType,
        Long relatedEntityId,
        Instant dueAt,
        Instant escalatedAt,
        Instant createdAt
) {
}
