package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for updating workspace user role or activation state.
 */
public record AppUserUpdateRequest(
        @NotBlank String role,
        boolean active
) {
}
