package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating an integration connection.
 */
public record IntegrationConnectionCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String channelType,
        @NotBlank @Size(max = 80) String provider,
        @NotBlank @Size(max = 40) String status
) {
}
