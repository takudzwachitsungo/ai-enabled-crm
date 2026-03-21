package com.dala.crm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating an SLA policy.
 */
public record SlaPolicyCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String priority,
        @NotNull @Min(1) @Max(720) Integer responseHours,
        @Size(max = 120) String defaultAssignee,
        boolean active
) {
}
