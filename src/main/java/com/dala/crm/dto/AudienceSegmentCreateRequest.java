package com.dala.crm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating an audience segment.
 */
public record AudienceSegmentCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String sourceType,
        @NotBlank @Size(max = 4000) String criteria,
        @Min(0) Integer estimatedSize,
        boolean active
) {
}
