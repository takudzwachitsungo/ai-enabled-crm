package com.dala.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request payload for creating an invoice.
 */
public record InvoiceCreateRequest(
        @NotNull Long accountId,
        @NotBlank @Size(max = 160) String invoiceNumber,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotBlank @Size(max = 40) String status,
        Instant dueAt
) {
}
