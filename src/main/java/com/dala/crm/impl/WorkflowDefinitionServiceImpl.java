package com.dala.crm.impl;

import com.dala.crm.dto.WorkflowBuilderCatalogResponse;
import com.dala.crm.dto.WorkflowBuilderCustomEntityOption;
import com.dala.crm.dto.WorkflowDefinitionCreateRequest;
import com.dala.crm.dto.WorkflowDefinitionDto;
import com.dala.crm.entity.CustomEntityDefinition;
import com.dala.crm.entity.WorkflowDefinition;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.CustomEntityDefinitionRepository;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.WorkflowDefinitionService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default workflow service implementation.
 */
@Service
@Transactional
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    private static final Set<String> SUPPORTED_TRIGGER_TYPES = Set.of(
            "LEAD_CREATED",
            "OPPORTUNITY_CREATED",
            "ACTIVITY_CREATED",
            "TICKET_CREATED",
            "CUSTOM_ENTITY_RECORD_CREATED"
    );
    private static final Set<String> SUPPORTED_ACTION_TYPES = Set.of(
            "CREATE_ACTIVITY",
            "CREATE_TASK",
            "CREATE_COMMUNICATION",
            "SEND_NOTIFICATION"
    );
    private static final List<String> SUPPORTED_TARGET_ENTITY_TYPES = List.of(
            "LEAD",
            "CONTACT",
            "ACCOUNT",
            "OPPORTUNITY",
            "TICKET",
            "QUOTE",
            "INVOICE",
            "CUSTOM_ENTITY"
    );

    private final WorkflowDefinitionRepository repository;
    private final CustomEntityDefinitionRepository customEntityDefinitionRepository;
    private final ObjectMapper objectMapper;

    public WorkflowDefinitionServiceImpl(
            WorkflowDefinitionRepository repository,
            CustomEntityDefinitionRepository customEntityDefinitionRepository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.customEntityDefinitionRepository = customEntityDefinitionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public WorkflowDefinitionDto create(WorkflowDefinitionCreateRequest request) {
        String tenantId = currentTenant();
        String triggerType = normalizeSupportedValue(request.triggerType(), SUPPORTED_TRIGGER_TYPES, "triggerType");
        String actionType = normalizeSupportedValue(request.actionType(), SUPPORTED_ACTION_TYPES, "actionType");
        String targetEntityType = normalizeTargetEntityType(request.targetEntityType());
        String targetEntityApiName = validateTargetEntityApiName(tenantId, targetEntityType, request.targetEntityApiName());
        String conditionsJson = normalizeJsonObjectOrNull(request.conditionsJson(), "conditionsJson");
        String actionConfigJson = normalizeJsonObjectOrNull(request.actionConfigJson(), "actionConfigJson");

        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setTenantId(tenantId);
        workflowDefinition.setName(request.name().trim());
        workflowDefinition.setTriggerType(triggerType);
        workflowDefinition.setTriggerFilter(trimToNull(request.triggerFilter()));
        workflowDefinition.setTargetEntityType(targetEntityType);
        workflowDefinition.setTargetEntityApiName(targetEntityApiName);
        workflowDefinition.setConditionsJson(conditionsJson);
        workflowDefinition.setActionType(actionType);
        workflowDefinition.setActionSubject(request.actionSubject().trim());
        workflowDefinition.setActionDetails(trimToNull(request.actionDetails()));
        workflowDefinition.setActionConfigJson(actionConfigJson);
        workflowDefinition.setActive(request.active());
        workflowDefinition.setCreatedAt(Instant.now());
        return toDto(repository.save(workflowDefinition));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowBuilderCatalogResponse getBuilderCatalog() {
        String tenantId = currentTenant();
        List<WorkflowBuilderCustomEntityOption> customEntities = customEntityDefinitionRepository
                .findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(CustomEntityDefinition::isActive)
                .map(definition -> new WorkflowBuilderCustomEntityOption(
                        definition.getId(),
                        definition.getApiName(),
                        definition.getName()
                ))
                .toList();
        return new WorkflowBuilderCatalogResponse(
                List.copyOf(SUPPORTED_TRIGGER_TYPES),
                List.copyOf(SUPPORTED_ACTION_TYPES),
                SUPPORTED_TARGET_ENTITY_TYPES,
                customEntities
        );
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
                workflowDefinition.getTargetEntityType(),
                workflowDefinition.getTargetEntityApiName(),
                workflowDefinition.getConditionsJson(),
                workflowDefinition.getActionType(),
                workflowDefinition.getActionSubject(),
                workflowDefinition.getActionDetails(),
                workflowDefinition.getActionConfigJson(),
                workflowDefinition.isActive(),
                workflowDefinition.getCreatedAt()
        );
    }

    private String normalizeTargetEntityType(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        String upperValue = normalized.toUpperCase(Locale.ROOT);
        if (!SUPPORTED_TARGET_ENTITY_TYPES.contains(upperValue)) {
            throw new BadRequestException("Unsupported targetEntityType: " + normalized);
        }
        return upperValue;
    }

    private String validateTargetEntityApiName(String tenantId, String targetEntityType, String value) {
        String trimmed = trimToNull(value);
        if (!"CUSTOM_ENTITY".equals(targetEntityType)) {
            return null;
        }
        if (trimmed == null) {
            throw new BadRequestException("targetEntityApiName is required when targetEntityType is CUSTOM_ENTITY");
        }
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        customEntityDefinitionRepository.findByTenantIdAndApiNameIgnoreCase(tenantId, normalized)
                .orElseThrow(() -> new BadRequestException("Unknown custom entity target: " + normalized));
        return normalized;
    }

    private String normalizeSupportedValue(String value, Set<String> supportedValues, String fieldName) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!supportedValues.contains(normalized)) {
            throw new BadRequestException("Unsupported " + fieldName + ": " + value.trim());
        }
        return normalized;
    }

    private String normalizeJsonObjectOrNull(String value, String fieldName) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(trimmed);
            if (node == null || !node.isObject()) {
                throw new BadRequestException(fieldName + " must be a valid JSON object");
            }
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException(fieldName + " must be a valid JSON object");
        }
    }
}
