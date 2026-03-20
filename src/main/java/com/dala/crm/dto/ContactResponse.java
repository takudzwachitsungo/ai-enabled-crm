package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a contact.
 */
public record ContactResponse(
        Long id,
        String fullName,
        String email,
        String companyName,
        Instant createdAt
) {
}
