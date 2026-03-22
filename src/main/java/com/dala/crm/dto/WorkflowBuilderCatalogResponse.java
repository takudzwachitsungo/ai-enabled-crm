package com.dala.crm.dto;

import java.util.List;

/**
 * Catalog response used by low-code workflow builder clients.
 */
public record WorkflowBuilderCatalogResponse(
        List<String> triggerTypes,
        List<String> actionTypes,
        List<String> targetEntityTypes,
        List<WorkflowBuilderCustomEntityOption> customEntities
) {
}
