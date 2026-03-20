package com.dala.crm.service;

import com.dala.crm.dto.WorkflowDefinitionCreateRequest;
import com.dala.crm.dto.WorkflowDefinitionDto;
import java.util.List;

/**
 * Public service contract for workflow module use cases.
 */
public interface WorkflowDefinitionService {

    /**
     * Creates a workflow definition within the current tenant.
     */
    WorkflowDefinitionDto create(WorkflowDefinitionCreateRequest request);

    /**
     * Returns the current tenant scope list for this module.
     */
    List<WorkflowDefinitionDto> list();
}
