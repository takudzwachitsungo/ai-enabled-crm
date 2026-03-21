package com.dala.crm.dto;

import java.time.Instant;

/**
 * Response returned after executing a campaign delivery run.
 */
public record CampaignDeliveryRunResponse(
        Long campaignId,
        String campaignName,
        String status,
        int deliveredCount,
        Instant executedAt
) {
}
