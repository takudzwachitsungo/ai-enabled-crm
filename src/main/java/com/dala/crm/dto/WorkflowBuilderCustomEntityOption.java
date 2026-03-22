package com.dala.crm.dto;

/**
 * Builder catalog entry for an available custom entity target.
 */
public record WorkflowBuilderCustomEntityOption(
        Long definitionId,
        String apiName,
        String name
) {
}
