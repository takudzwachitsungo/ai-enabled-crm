package com.dala.crm.dto;

/**
 * Top-level KPI summary for the current tenant dashboard.
 */
public record DashboardSummaryResponse(
        long leadCount,
        long contactCount,
        long accountCount,
        long opportunityCount,
        long activityCount,
        long activeWorkflowCount,
        long integrationCount,
        long communicationCount,
        long aiInteractionCount
) {
}
