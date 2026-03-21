package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for generating a scheduled report snapshot.
 */
public record ReportSnapshotCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String reportType,
        @NotBlank @Size(max = 40) String deliveryChannel,
        @NotBlank @Size(max = 40) String scheduleCadence
) {
}
