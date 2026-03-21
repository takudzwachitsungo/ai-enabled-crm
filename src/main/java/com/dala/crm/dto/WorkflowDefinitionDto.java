package com.dala.crm.dto;

/**
 * API representation of a workflow definition.
 */
public record WorkflowDefinitionDto(
        Long id,
        String name,
        String triggerType,
        String triggerFilter,
        String actionType,
        String actionSubject,
        String actionDetails,
        boolean active,
        java.time.Instant createdAt
) {
}
