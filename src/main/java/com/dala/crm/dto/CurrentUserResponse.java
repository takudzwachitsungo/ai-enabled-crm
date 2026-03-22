package com.dala.crm.dto;

import java.util.List;

/**
 * API representation of the currently authenticated user and granted authorities.
 */
public record CurrentUserResponse(
        String tenantId,
        String tenantName,
        String fullName,
        String email,
        String username,
        List<String> authorities
) {
}
