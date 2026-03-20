package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a traceable AI summary.
 */
public record AiSummarizeRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String sourceType,
        Long sourceId,
        @NotBlank @Size(max = 4000) String text
) {
}
