package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Quote response payload.
 */
public record QuoteResponse(
        Long id,
        Long accountId,
        String accountName,
        String name,
        BigDecimal amount,
        String status,
        Instant validUntil,
        Instant createdAt
) {
}
