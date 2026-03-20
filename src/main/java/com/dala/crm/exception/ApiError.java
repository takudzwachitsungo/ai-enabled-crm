package com.dala.crm.exception;

import java.time.Instant;

/**
 * Standardized error payload returned by API exception handlers.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
