package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a canned response.
 */
public record CannedResponseCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 80) String channelType,
        @Size(max = 80) String category,
        @NotBlank @Size(max = 4000) String body
) {
}
