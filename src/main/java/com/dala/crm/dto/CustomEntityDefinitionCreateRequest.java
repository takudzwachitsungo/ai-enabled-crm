package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a custom entity definition.
 */
public record CustomEntityDefinitionCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 100) String apiName,
        @Size(max = 160) String pluralLabel,
        @NotBlank @Size(max = 8000) String fieldSchemaJson,
        boolean active
) {
}
