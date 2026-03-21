package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for quote and invoice status updates.
 */
public record CommerceStatusUpdateRequest(
        @NotBlank @Size(max = 40) String status,
        @Size(max = 4000) String note
) {
}
