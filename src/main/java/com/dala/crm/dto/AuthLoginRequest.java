package com.dala.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for tenant-aware login.
 */
public record AuthLoginRequest(
        @NotBlank String tenantId,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
