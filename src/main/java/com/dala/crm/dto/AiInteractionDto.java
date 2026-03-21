package com.dala.crm.dto;

/**
 * API representation of a traceable AI interaction.
 */
public record AiInteractionDto(
        Long id,
        String name,
        String operationType,
        String sourceType,
        Long sourceId,
        String promptText,
        String outputText,
        String modelName,
        java.time.Instant createdAt
) {
}
