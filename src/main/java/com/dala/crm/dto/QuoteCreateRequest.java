package com.dala.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request payload for creating a quote.
 */
public record QuoteCreateRequest(
        @NotNull Long accountId,
        @NotBlank @Size(max = 160) String name,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotBlank @Size(max = 40) String status,
        Instant validUntil
) {
}
