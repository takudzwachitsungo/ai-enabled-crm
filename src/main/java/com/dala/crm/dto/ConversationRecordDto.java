package com.dala.crm.dto;

/**
 * API representation of a communication record.
 */
public record ConversationRecordDto(
        Long id,
        String name,
        String channelType,
        String direction,
        String participant,
        String subject,
        String messageBody,
        String relatedEntityType,
        Long relatedEntityId,
        java.time.Instant createdAt
) {
}
