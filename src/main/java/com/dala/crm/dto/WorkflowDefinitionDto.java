package com.dala.crm.dto;

/**
 * API representation of a workflow definition.
 */
public record WorkflowDefinitionDto(
        Long id,
        String name,
        String triggerType,
        String triggerFilter,
        String targetEntityType,
        String targetEntityApiName,
        String conditionsJson,
        String actionType,
        String actionSubject,
        String actionDetails,
        String actionConfigJson,
        boolean active,
        java.time.Instant createdAt
) {
}
