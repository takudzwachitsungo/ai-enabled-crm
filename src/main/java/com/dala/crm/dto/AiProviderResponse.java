package com.dala.crm.dto;

/**
 * Response contract returned by the dedicated Python AI service.
 */
public record AiProviderResponse(
        String provider,
        String model,
        String output
) {
}
