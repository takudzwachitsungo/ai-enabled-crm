package com.dala.crm.service;

import com.dala.crm.dto.WorkflowDefinitionDto;
import java.util.List;

/**
 * Public service contract for workflow module use cases.
 */
public interface WorkflowDefinitionService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<WorkflowDefinitionDto> list();
}
