package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for account churn risk analysis.
 */
public record AiChurnRiskRequest(
        @NotBlank @Size(max = 160) String name,
        @NotNull Long accountId
) {
}
