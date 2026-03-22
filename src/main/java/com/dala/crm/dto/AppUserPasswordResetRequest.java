package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for resetting a workspace user's password.
 */
public record AppUserPasswordResetRequest(
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
