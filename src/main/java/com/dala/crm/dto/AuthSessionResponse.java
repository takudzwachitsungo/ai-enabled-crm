package com.dala.crm.dto;

import java.util.List;

/**
 * Response payload describing an authenticated tenant session.
 */
public record AuthSessionResponse(
        String tenantId,
        String tenantName,
        String fullName,
        String email,
        List<String> authorities,
        String accessToken,
        String tokenType,
        String expiresAt
) {
}
