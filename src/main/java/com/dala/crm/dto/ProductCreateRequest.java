package com.dala.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request payload for creating a product.
 */
public record ProductCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 4000) String description,
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
        @NotBlank @Size(max = 40) String status
) {
}
