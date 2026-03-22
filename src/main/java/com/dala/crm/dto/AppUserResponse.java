package com.dala.crm.dto;

import java.time.Instant;

/**
 * Response payload for tenant user management.
 */
public record AppUserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        boolean active,
        Instant createdAt
) {
}
