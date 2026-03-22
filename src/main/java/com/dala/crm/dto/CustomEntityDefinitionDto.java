package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a custom entity definition.
 */
public record CustomEntityDefinitionDto(
        Long id,
        String name,
        String apiName,
        String pluralLabel,
        String fieldSchemaJson,
        boolean active,
        Instant createdAt
) {
}
