package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update request for custom entity definitions.
 */
public record CustomEntityDefinitionUpdateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 160) String pluralLabel,
        @NotBlank @Size(max = 8000) String fieldSchemaJson,
        boolean active
) {
}
