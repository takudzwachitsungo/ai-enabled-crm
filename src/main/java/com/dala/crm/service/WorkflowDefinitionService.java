package com.dala.crm.service;

import com.dala.crm.dto.WorkflowDefinitionCreateRequest;
import com.dala.crm.dto.WorkflowBuilderCatalogResponse;
import com.dala.crm.dto.WorkflowDefinitionDto;
import java.util.List;
import com.dala.crm.dto.WorkflowDefinitionUpdateRequest;

/**
 * Public service contract for workflow module use cases.
 */
public interface WorkflowDefinitionService {

    /**
     * Creates a workflow definition within the current tenant.
     */
    WorkflowDefinitionDto create(WorkflowDefinitionCreateRequest request);

    WorkflowDefinitionDto update(Long id, WorkflowDefinitionUpdateRequest request);

    /**
     * Returns builder metadata to help low-code clients configure workflows.
     */
    WorkflowBuilderCatalogResponse getBuilderCatalog();

    /**
     * Returns the current tenant scope list for this module.
     */
    List<WorkflowDefinitionDto> list();
}
