package com.dala.crm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request payload for tenant-aware assistant chat.
 */
public record AiChatRequest(
        @Size(max = 160) String name,
        @NotBlank @Size(max = 4000) String message,
        @Valid List<AiChatMessageRequest> conversation
) {
}
