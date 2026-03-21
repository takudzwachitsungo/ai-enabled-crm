package com.dala.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request payload for creating an opportunity.
 */
public record OpportunityCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 160) String accountName,
        @NotNull @DecimalMin(value = "0.00") BigDecimal amount,
        @NotBlank @Size(max = 40) String stage
) {
}
