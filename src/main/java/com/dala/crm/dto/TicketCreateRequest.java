package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a service ticket.
 */
public record TicketCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 4000) String description,
        @NotBlank @Size(max = 40) String priority,
        @Size(max = 120) String assignee,
        @Size(max = 40) String sourceChannel,
        @Size(max = 40) String relatedEntityType,
        Long relatedEntityId
) {
}
