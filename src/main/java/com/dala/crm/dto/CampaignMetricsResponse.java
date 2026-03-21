package com.dala.crm.dto;

/**
 * Tenant-visible campaign metrics summary.
 */
public record CampaignMetricsResponse(
        long totalCampaigns,
        long draftCampaigns,
        long scheduledCampaigns,
        long sentCampaigns,
        long totalDeliveredRecipients
) {
}
