package com.dala.crm.dto;

import java.time.Instant;

/**
 * API representation of a canned response.
 */
public record CannedResponseResponse(
        Long id,
        String title,
        String channelType,
        String category,
        String body,
        Instant createdAt
) {
}
