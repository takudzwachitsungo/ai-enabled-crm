package com.dala.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for tenant registration and admin user bootstrap.
 */
public record AuthSignupRequest(
        @NotBlank @Size(max = 160) String companyName,
        @NotBlank @Size(max = 100) String tenantId,
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
