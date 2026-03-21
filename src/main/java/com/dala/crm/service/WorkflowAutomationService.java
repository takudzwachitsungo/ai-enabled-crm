package com.dala.crm.service;

/**
 * Internal automation executor for workflow triggers.
 */
public interface WorkflowAutomationService {

    /**
     * Executes active workflows for the provided trigger in the current tenant scope.
     */
    void execute(String triggerType, String relatedEntityType, Long relatedEntityId, String relatedEntityName);
}
