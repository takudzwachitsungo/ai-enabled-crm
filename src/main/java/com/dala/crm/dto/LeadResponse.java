package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a lead.
 */
public record LeadResponse(
        Long id,
        String fullName,
        String email,
        String status,
        Instant createdAt
) {
}
