package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Invoice response payload.
 */
public record InvoiceResponse(
        Long id,
        Long accountId,
        String accountName,
        String invoiceNumber,
        BigDecimal amount,
        String status,
        Instant dueAt,
        Instant createdAt
) {
}
