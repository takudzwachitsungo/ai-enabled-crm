package com.dala.crm.impl;

import com.dala.crm.entity.Activity;
import com.dala.crm.entity.WorkflowDefinition;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.WorkflowAutomationService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Executes lightweight workflow automation for key CRM events.
 */
@Service
@Transactional
public class WorkflowAutomationServiceImpl implements WorkflowAutomationService {

    private static final String CREATE_ACTIVITY = "CREATE_ACTIVITY";

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public WorkflowAutomationServiceImpl(
            WorkflowDefinitionRepository workflowDefinitionRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public void execute(String triggerType, String relatedEntityType, Long relatedEntityId, String relatedEntityName) {
        String tenantId = currentTenant();
        List<WorkflowDefinition> workflows = workflowDefinitionRepository
                .findByTenantIdAndActiveTrueAndTriggerType(tenantId, triggerType);

        for (WorkflowDefinition workflow : workflows) {
            if (!matchesFilter(workflow.getTriggerFilter(), relatedEntityName)) {
                continue;
            }
            if (CREATE_ACTIVITY.equalsIgnoreCase(workflow.getActionType())) {
                Activity activity = new Activity();
                activity.setTenantId(tenantId);
                activity.setType("WORKFLOW");
                activity.setSubject(workflow.getActionSubject());
                activity.setRelatedEntityType(relatedEntityType);
                activity.setRelatedEntityId(relatedEntityId);
                activity.setDetails(workflow.getActionDetails());
                activity.setCreatedAt(Instant.now());
                Activity savedActivity = activityRepository.save(activity);
                auditLogService.record(
                        "WORKFLOW_EXECUTED",
                        "WORKFLOW",
                        workflow.getId(),
                        "Workflow " + workflow.getName() + " created activity " + savedActivity.getSubject()
                );
            }
        }
    }

    private boolean matchesFilter(String triggerFilter, String relatedEntityName) {
        if (triggerFilter == null || triggerFilter.isBlank()) {
            return true;
        }
        String candidate = relatedEntityName == null ? "" : relatedEntityName;
        return candidate.toLowerCase(Locale.ROOT).contains(triggerFilter.toLowerCase(Locale.ROOT));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }
}
