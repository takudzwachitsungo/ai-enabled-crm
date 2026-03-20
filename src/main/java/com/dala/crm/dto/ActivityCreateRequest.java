package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a CRM activity.
 */
public record ActivityCreateRequest(
        @NotBlank @Size(max = 40) String type,
        @NotBlank @Size(max = 160) String subject,
        @NotBlank @Size(max = 40) String relatedEntityType,
        @NotNull Long relatedEntityId,
        @Size(max = 4000) String details
) {
}
