package com.dala.crm.dto;

/**
 * Expanded analytics payload for the current tenant dashboard.
 */
public record DashboardAnalyticsResponse(
        long publishedKnowledgeArticleCount,
        long cannedResponseCount,
        long activeAudienceSegmentCount,
        long draftCampaignCount,
        long scheduledCampaignCount,
        long openTicketCount,
        long escalatedTicketCount,
        long reportSnapshotCount
) {
}
