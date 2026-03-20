package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating an account.
 */
public record AccountCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 120) String industry,
        @Size(max = 200) String website
) {
}
