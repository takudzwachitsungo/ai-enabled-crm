package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Product catalog response payload.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal unitPrice,
        String status,
        Instant createdAt
) {
}
