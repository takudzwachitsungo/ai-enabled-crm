package com.dala.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a simple workflow definition.
 */
public record WorkflowDefinitionCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 60) String triggerType,
        @Size(max = 160) String triggerFilter,
        @Size(max = 60) String targetEntityType,
        @Size(max = 100) String targetEntityApiName,
        @Size(max = 4000) String conditionsJson,
        @NotBlank @Size(max = 60) String actionType,
        @NotBlank @Size(max = 160) String actionSubject,
        @Size(max = 4000) String actionDetails,
        @Size(max = 4000) String actionConfigJson,
        boolean active
) {
}
