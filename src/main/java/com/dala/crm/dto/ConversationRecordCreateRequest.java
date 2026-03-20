package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a communication record.
 */
public record ConversationRecordCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String channelType,
        @NotBlank @Size(max = 40) String direction,
        @NotBlank @Size(max = 160) String participant,
        @Size(max = 160) String subject,
        @NotBlank @Size(max = 4000) String messageBody,
        @Size(max = 40) String relatedEntityType,
        Long relatedEntityId
) {
}
