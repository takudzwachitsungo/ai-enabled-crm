package com.dala.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request payload for creating a commerce event.
 */
public record CommerceEventCreateRequest(
        @NotNull Long integrationConnectionId,
        @NotBlank @Size(max = 40) String eventType,
        @NotBlank @Size(max = 120) String sourceReference,
        @Size(max = 40) String relatedEntityType,
        Long relatedEntityId,
        @DecimalMin("0.00") BigDecimal amount,
        @NotBlank @Size(max = 4000) String payload
) {
}
