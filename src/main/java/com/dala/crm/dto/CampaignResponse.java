package com.dala.crm.dto;

import java.time.Instant;

/**
 * Campaign response payload.
 */
public record CampaignResponse(
        Long id,
        String name,
        String channelType,
        String status,
        Long audienceSegmentId,
        String audienceSegmentName,
        String subject,
        String body,
        Instant scheduledAt,
        int deliveredCount,
        Instant lastExecutedAt,
        Instant createdAt
) {
}
