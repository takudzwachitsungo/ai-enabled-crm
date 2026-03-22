package com.dala.crm.dto;

import java.time.Instant;

/**
 * Result payload for a commerce renewal automation run.
 */
public record RenewalAutomationRunResponse(
        long renewalCandidateCount,
        long expiringQuoteCount,
        long generatedActivityCount,
        Instant generatedAt
) {
}
