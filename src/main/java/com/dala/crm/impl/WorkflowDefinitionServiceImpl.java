package com.dala.crm.impl;

import com.dala.crm.dto.WorkflowDefinitionCreateRequest;
import com.dala.crm.dto.WorkflowDefinitionDto;
import com.dala.crm.entity.WorkflowDefinition;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.WorkflowDefinitionService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default workflow service implementation.
 */
@Service
@Transactional
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    private final WorkflowDefinitionRepository repository;

    public WorkflowDefinitionServiceImpl(WorkflowDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkflowDefinitionDto create(WorkflowDefinitionCreateRequest request) {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setTenantId(currentTenant());
        workflowDefinition.setName(request.name().trim());
        workflowDefinition.setTriggerType(request.triggerType().trim());
        workflowDefinition.setTriggerFilter(trimToNull(request.triggerFilter()));
        workflowDefinition.setActionType(request.actionType().trim());
        workflowDefinition.setActionSubject(request.actionSubject().trim());
        workflowDefinition.setActionDetails(trimToNull(request.actionDetails()));
        workflowDefinition.setActive(request.active());
        workflowDefinition.setCreatedAt(Instant.now());
        return toDto(repository.save(workflowDefinition));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDefinitionDto> list() {
        return repository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDto)
                .toList();
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private WorkflowDefinitionDto toDto(WorkflowDefinition workflowDefinition) {
        return new WorkflowDefinitionDto(
                workflowDefinition.getId(),
                workflowDefinition.getName(),
                workflowDefinition.getTriggerType(),
                workflowDefinition.getTriggerFilter(),
                workflowDefinition.getActionType(),
                workflowDefinition.getActionSubject(),
                workflowDefinition.getActionDetails(),
                workflowDefinition.isActive(),
                workflowDefinition.getCreatedAt()
        );
    }
}
