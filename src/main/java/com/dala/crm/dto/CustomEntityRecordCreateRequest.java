package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a custom entity record.
 */
public record CustomEntityRecordCreateRequest(
        @NotBlank @Size(max = 8000) String recordDataJson
) {
}
