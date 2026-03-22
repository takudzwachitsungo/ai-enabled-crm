package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a custom entity record.
 */
public record CustomEntityRecordDto(
        Long id,
        Long definitionId,
        String recordDataJson,
        Instant createdAt
) {
}
