package com.dala.crm.dto;

import java.time.Instant;

/**
 * Scheduled report snapshot payload.
 */
public record ReportSnapshotDto(
        Long id,
        String name,
        String reportType,
        String deliveryChannel,
        String scheduleCadence,
        String status,
        String snapshotPayload,
        Instant generatedAt,
        Instant createdAt
) {
}
