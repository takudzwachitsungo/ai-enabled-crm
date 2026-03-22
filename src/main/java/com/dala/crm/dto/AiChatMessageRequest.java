package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Single conversation turn sent to the tenant assistant.
 */
public record AiChatMessageRequest(
        @NotBlank @Size(max = 20) String role,
        @NotBlank @Size(max = 4000) String content
) {
}
