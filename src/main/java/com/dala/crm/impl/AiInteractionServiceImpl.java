package com.dala.crm.impl;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AiSummarizeRequest;
import com.dala.crm.entity.AiInteraction;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.AiInteractionService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default aiassistant service implementation.
 */
@Service
@Transactional
public class AiInteractionServiceImpl implements AiInteractionService {

    private final AiInteractionRepository repository;
    private final AuditLogService auditLogService;

    public AiInteractionServiceImpl(
            AiInteractionRepository repository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @Override
    public AiInteractionDto summarize(AiSummarizeRequest request) {
        String promptText = request.text().trim();
        String normalized = promptText.replaceAll("\\s+", " ").trim();
        String summary = normalized.length() <= 180
                ? normalized
                : normalized.substring(0, 177) + "...";
        String output = "Summary: " + summary;
        return saveInteraction(
                request.name().trim(),
                "SUMMARIZE",
                request.sourceType().trim(),
                request.sourceId(),
                promptText,
                output
        );
    }

    @Override
    public AiInteractionDto draft(AiDraftRequest request) {
        String channel = normalizeOrDefault(request.channel(), "EMAIL");
        String tone = normalizeOrDefault(request.tone(), "PROFESSIONAL");
        String instructions = request.instructions().trim();
        String output = "Draft (" + channel + ", " + tone + "): " + instructions;
        return saveInteraction(
                request.name().trim(),
                "DRAFT",
                request.sourceType().trim(),
                request.sourceId(),
                instructions,
                output
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiInteractionDto> list() {
        return repository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDto)
                .toList();
    }

    private AiInteractionDto saveInteraction(
            String name,
            String operationType,
            String sourceType,
            Long sourceId,
            String promptText,
            String outputText
    ) {
        AiInteraction interaction = new AiInteraction();
        interaction.setTenantId(currentTenant());
        interaction.setName(name);
        interaction.setOperationType(operationType);
        interaction.setSourceType(sourceType);
        interaction.setSourceId(sourceId);
        interaction.setPromptText(promptText);
        interaction.setOutputText(outputText);
        interaction.setModelName("local-mock");
        interaction.setCreatedAt(Instant.now());
        AiInteraction savedInteraction = repository.save(interaction);
        auditLogService.record(
                "AI_" + operationType,
                "AI_INTERACTION",
                savedInteraction.getId(),
                "Generated " + operationType.toLowerCase() + " output for " + name
        );
        return toDto(savedInteraction);
    }

    private String normalizeOrDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private AiInteractionDto toDto(AiInteraction interaction) {
        return new AiInteractionDto(
                interaction.getId(),
                interaction.getName(),
                interaction.getOperationType(),
                interaction.getSourceType(),
                interaction.getSourceId(),
                interaction.getPromptText(),
                interaction.getOutputText(),
                interaction.getModelName(),
                interaction.getCreatedAt()
        );
    }
}
