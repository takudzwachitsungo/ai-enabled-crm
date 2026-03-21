package com.dala.crm.impl;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiAccountHealthRequest;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AiLeadScoreRequest;
import com.dala.crm.dto.AiSummarizeRequest;
import com.dala.crm.entity.AiInteraction;
import com.dala.crm.entity.Account;
import com.dala.crm.entity.Lead;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.LeadRepository;
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
    private final LeadRepository leadRepository;
    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final ConversationRecordRepository conversationRecordRepository;
    private final AuditLogService auditLogService;

    public AiInteractionServiceImpl(
            AiInteractionRepository repository,
            LeadRepository leadRepository,
            AccountRepository accountRepository,
            ActivityRepository activityRepository,
            ConversationRecordRepository conversationRecordRepository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.accountRepository = accountRepository;
        this.activityRepository = activityRepository;
        this.conversationRecordRepository = conversationRecordRepository;
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
    public AiInteractionDto scoreLead(AiLeadScoreRequest request) {
        String tenantId = currentTenant();
        Lead lead = currentLead(tenantId, request.leadId());
        long activityCount = activityRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "LEAD", lead.getId());
        long communicationCount = conversationRecordRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "LEAD", lead.getId());

        int score = 35;
        if ("QUALIFIED".equalsIgnoreCase(lead.getStatus())) {
            score += 30;
        } else if ("NEW".equalsIgnoreCase(lead.getStatus())) {
            score += 10;
        }
        score += (int) Math.min(activityCount * 10, 20);
        score += (int) Math.min(communicationCount * 5, 15);
        score = Math.min(score, 100);

        String prompt = "Score lead " + lead.getFullName()
                + " with status " + lead.getStatus()
                + ", activities=" + activityCount
                + ", communications=" + communicationCount;
        String output = "Lead score " + score + "/100 for " + lead.getFullName()
                + ". Rationale: status=" + lead.getStatus()
                + ", activities=" + activityCount
                + ", communications=" + communicationCount + ".";

        return saveInteraction(
                request.name().trim(),
                "LEAD_SCORE",
                "LEAD",
                lead.getId(),
                prompt,
                output
        );
    }

    @Override
    public AiInteractionDto assessAccountHealth(AiAccountHealthRequest request) {
        String tenantId = currentTenant();
        Account account = currentAccount(tenantId, request.accountId());
        long activityCount = activityRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());
        long communicationCount = conversationRecordRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());

        int health = 45;
        if (account.getWebsite() != null && !account.getWebsite().isBlank()) {
            health += 10;
        }
        if (account.getIndustry() != null && !account.getIndustry().isBlank()) {
            health += 10;
        }
        health += (int) Math.min(activityCount * 8, 20);
        health += (int) Math.min(communicationCount * 5, 15);
        health = Math.min(health, 100);

        String level = health >= 75 ? "HEALTHY" : health >= 50 ? "WATCH" : "AT_RISK";
        String prompt = "Assess health for account " + account.getName()
                + ", activities=" + activityCount
                + ", communications=" + communicationCount
                + ", industry=" + account.getIndustry();
        String output = "Account health " + health + "/100 (" + level + ") for " + account.getName()
                + ". Signals: activities=" + activityCount
                + ", communications=" + communicationCount
                + ", industry=" + account.getIndustry()
                + ", websitePresent=" + (account.getWebsite() != null) + ".";

        return saveInteraction(
                request.name().trim(),
                "ACCOUNT_HEALTH",
                "ACCOUNT",
                account.getId(),
                prompt,
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

    private Lead currentLead(String tenantId, Long leadId) {
        return leadRepository.findById(leadId)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Lead not found: " + leadId));
    }

    private Account currentAccount(String tenantId, Long accountId) {
        return accountRepository.findById(accountId)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Account not found: " + accountId));
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
