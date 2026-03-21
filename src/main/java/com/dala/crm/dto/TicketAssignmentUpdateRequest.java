package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating ticket assignment.
 */
public record TicketAssignmentUpdateRequest(
        @NotBlank @Size(max = 120) String assignee,
        @Size(max = 4000) String note
) {
}
