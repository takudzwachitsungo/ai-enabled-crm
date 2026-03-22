package com.dala.crm.impl;

import com.dala.crm.dto.CustomEntityDefinitionCreateRequest;
import com.dala.crm.dto.CustomEntityDefinitionDto;
import com.dala.crm.dto.CustomEntityRecordCreateRequest;
import com.dala.crm.dto.CustomEntityRecordDto;
import com.dala.crm.entity.CustomEntityDefinition;
import com.dala.crm.entity.CustomEntityRecord;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.CustomEntityDefinitionNotFoundException;
import com.dala.crm.exception.CustomEntityRecordNotFoundException;
import com.dala.crm.repo.CustomEntityDefinitionRepository;
import com.dala.crm.repo.CustomEntityRecordRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.CustomEntityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default service implementation for custom entity platform features.
 */
@Service
@Transactional
public class CustomEntityServiceImpl implements CustomEntityService {

    private final CustomEntityDefinitionRepository definitionRepository;
    private final CustomEntityRecordRepository recordRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public CustomEntityServiceImpl(
            CustomEntityDefinitionRepository definitionRepository,
            CustomEntityRecordRepository recordRepository,
            AuditLogService auditLogService,
            ObjectMapper objectMapper
    ) {
        this.definitionRepository = definitionRepository;
        this.recordRepository = recordRepository;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    public CustomEntityDefinitionDto createDefinition(CustomEntityDefinitionCreateRequest request) {
        String tenantId = currentTenant();
        String apiName = normalizeApiName(request.apiName());
        validateSchema(request.fieldSchemaJson());
        definitionRepository.findByTenantIdAndApiNameIgnoreCase(tenantId, apiName)
                .ifPresent(existing -> {
                    throw new BadRequestException("Custom entity apiName already exists: " + apiName);
                });

        CustomEntityDefinition definition = new CustomEntityDefinition();
        definition.setTenantId(tenantId);
        definition.setName(request.name().trim());
        definition.setApiName(apiName);
        definition.setPluralLabel(trimToNull(request.pluralLabel()));
        definition.setFieldSchemaJson(request.fieldSchemaJson().trim());
        definition.setActive(request.active());
        definition.setCreatedAt(Instant.now());

        CustomEntityDefinition savedDefinition = definitionRepository.save(definition);
        auditLogService.record(
                "CREATE",
                "CUSTOM_ENTITY_DEFINITION",
                savedDefinition.getId(),
                "Created custom entity definition " + savedDefinition.getApiName()
        );
        return toDefinitionDto(savedDefinition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomEntityDefinitionDto> listDefinitions() {
        return definitionRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDefinitionDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomEntityDefinitionDto getDefinition(Long id) {
        return toDefinitionDto(currentDefinition(id));
    }

    @Override
    public CustomEntityRecordDto createRecord(Long definitionId, CustomEntityRecordCreateRequest request) {
        CustomEntityDefinition definition = currentDefinition(definitionId);
        validateRecordAgainstSchema(definition.getFieldSchemaJson(), request.recordDataJson());

        CustomEntityRecord record = new CustomEntityRecord();
        record.setTenantId(definition.getTenantId());
        record.setDefinitionId(definition.getId());
        record.setRecordDataJson(request.recordDataJson().trim());
        record.setCreatedAt(Instant.now());

        CustomEntityRecord savedRecord = recordRepository.save(record);
        auditLogService.record(
                "CREATE",
                "CUSTOM_ENTITY_RECORD",
                savedRecord.getId(),
                "Created custom entity record for " + definition.getApiName()
        );
        return toRecordDto(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomEntityRecordDto> listRecords(Long definitionId) {
        CustomEntityDefinition definition = currentDefinition(definitionId);
        return recordRepository.findByTenantIdAndDefinitionIdOrderByCreatedAtDesc(definition.getTenantId(), definition.getId()).stream()
                .map(this::toRecordDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomEntityRecordDto getRecord(Long definitionId, Long recordId) {
        CustomEntityDefinition definition = currentDefinition(definitionId);
        CustomEntityRecord record = recordRepository.findById(recordId)
                .filter(candidate -> candidate.getTenantId().equals(definition.getTenantId()))
                .filter(candidate -> candidate.getDefinitionId().equals(definition.getId()))
                .orElseThrow(() -> new CustomEntityRecordNotFoundException(recordId));
        return toRecordDto(record);
    }

    private CustomEntityDefinition currentDefinition(Long id) {
        String tenantId = currentTenant();
        return definitionRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new CustomEntityDefinitionNotFoundException(id));
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

    private String normalizeApiName(String value) {
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        if (!trimmed.matches("[a-z][a-z0-9_]*")) {
            throw new BadRequestException("apiName must match pattern [a-z][a-z0-9_]*");
        }
        return trimmed;
    }

    private void validateSchema(String schemaJson) {
        JsonNode schemaNode = parseObject(schemaJson, "fieldSchemaJson");
        if (!schemaNode.fieldNames().hasNext()) {
            throw new BadRequestException("fieldSchemaJson must define at least one field");
        }
    }

    private void validateRecordAgainstSchema(String schemaJson, String recordJson) {
        JsonNode schemaNode = parseObject(schemaJson, "fieldSchemaJson");
        JsonNode recordNode = parseObject(recordJson, "recordDataJson");
        Set<String> allowedFields = new LinkedHashSet<>();
        Iterator<String> fieldNames = schemaNode.fieldNames();
        while (fieldNames.hasNext()) {
            allowedFields.add(fieldNames.next());
        }

        Iterator<String> recordFields = recordNode.fieldNames();
        while (recordFields.hasNext()) {
            String fieldName = recordFields.next();
            if (!allowedFields.contains(fieldName)) {
                throw new BadRequestException("recordDataJson contains undefined field: " + fieldName);
            }
        }
    }

    private JsonNode parseObject(String json, String fieldName) {
        try {
            JsonNode node = objectMapper.readTree(json.trim());
            if (node == null || !node.isObject()) {
                throw new BadRequestException(fieldName + " must be a valid JSON object");
            }
            return node;
        } catch (JsonProcessingException ex) {
            throw new BadRequestException(fieldName + " must be a valid JSON object");
        }
    }

    private CustomEntityDefinitionDto toDefinitionDto(CustomEntityDefinition definition) {
        return new CustomEntityDefinitionDto(
                definition.getId(),
                definition.getName(),
                definition.getApiName(),
                definition.getPluralLabel(),
                definition.getFieldSchemaJson(),
                definition.isActive(),
                definition.getCreatedAt()
        );
    }

    private CustomEntityRecordDto toRecordDto(CustomEntityRecord record) {
        return new CustomEntityRecordDto(
                record.getId(),
                record.getDefinitionId(),
                record.getRecordDataJson(),
                record.getCreatedAt()
        );
    }
}
