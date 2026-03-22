package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for tenant-aware login.
 */
public record AuthLoginRequest(
        @NotBlank String tenantId,
        @NotBlank @Size(max = 160) String email,
        @NotBlank String password
) {
}
