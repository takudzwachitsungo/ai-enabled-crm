package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update request for installed integration connections.
 */
public record IntegrationConnectionUpdateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String status
) {
}
