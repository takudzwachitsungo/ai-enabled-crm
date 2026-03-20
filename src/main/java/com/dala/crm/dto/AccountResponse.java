package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of an account.
 */
public record AccountResponse(
        Long id,
        String name,
        String industry,
        String website,
        Instant createdAt
) {
}
