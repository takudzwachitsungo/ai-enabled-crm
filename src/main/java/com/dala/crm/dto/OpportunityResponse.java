package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * API representation of an opportunity.
 */
public record OpportunityResponse(
        Long id,
        String name,
        String accountName,
        BigDecimal amount,
        String stage,
        Instant createdAt
) {
}
