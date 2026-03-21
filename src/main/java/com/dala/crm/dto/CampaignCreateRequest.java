package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * Request payload for creating a campaign.
 */
public record CampaignCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String channelType,
        @NotBlank @Size(max = 40) String status,
        @NotNull Long audienceSegmentId,
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(max = 4000) String body,
        Instant scheduledAt
) {
}
