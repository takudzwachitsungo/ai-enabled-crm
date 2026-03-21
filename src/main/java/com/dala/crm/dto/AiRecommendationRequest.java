package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for AI recommendation generation.
 */
public record AiRecommendationRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String sourceType,
        @NotNull Long sourceId,
        @Size(max = 200) String objective,
        boolean autoCreateActivity
) {
}
