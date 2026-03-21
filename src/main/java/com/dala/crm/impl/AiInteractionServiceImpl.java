package com.dala.crm.impl;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiAccountHealthRequest;
import com.dala.crm.dto.AiChurnRiskRequest;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AiLeadScoreRequest;
import com.dala.crm.dto.AiProviderResponse;
import com.dala.crm.dto.AiRecommendationRequest;
import com.dala.crm.dto.AiSummarizeRequest;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.AiInteraction;
import com.dala.crm.entity.Account;
import com.dala.crm.entity.Lead;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.CommerceEventRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.LeadRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.AiGatewayClient;
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
    private final CommerceEventRepository commerceEventRepository;
    private final AiGatewayClient aiGatewayClient;
    private final AuditLogService auditLogService;

    public AiInteractionServiceImpl(
            AiInteractionRepository repository,
            LeadRepository leadRepository,
            AccountRepository accountRepository,
            ActivityRepository activityRepository,
            ConversationRecordRepository conversationRecordRepository,
            CommerceEventRepository commerceEventRepository,
            AiGatewayClient aiGatewayClient,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.accountRepository = accountRepository;
        this.activityRepository = activityRepository;
        this.conversationRecordRepository = conversationRecordRepository;
        this.commerceEventRepository = commerceEventRepository;
        this.aiGatewayClient = aiGatewayClient;
        this.auditLogService = auditLogService;
    }

    @Override
    public AiInteractionDto summarize(AiSummarizeRequest request) {
        String promptText = request.text().trim();
        AiProviderResponse aiResponse = aiGatewayClient.summarize(promptText, 4)
                .orElseGet(() -> new AiProviderResponse("spring-fallback", "local-summary", fallbackSummary(promptText)));
        return saveInteraction(
                request.name().trim(),
                "SUMMARIZE",
                request.sourceType().trim(),
                request.sourceId(),
                promptText,
                aiResponse.output(),
                modelName(aiResponse)
        );
    }

    @Override
    public AiInteractionDto draft(AiDraftRequest request) {
        String channel = normalizeOrDefault(request.channel(), "EMAIL");
        String tone = normalizeOrDefault(request.tone(), "PROFESSIONAL");
        String instructions = request.instructions().trim();
        AiProviderResponse aiResponse = aiGatewayClient.draft(
                "Draft a " + channel + " response",
                instructions,
                tone.toLowerCase()
        ).orElseGet(() -> new AiProviderResponse(
                "spring-fallback",
                "local-draft",
                "Draft (" + channel + ", " + tone + "): " + instructions
        ));
        return saveInteraction(
                request.name().trim(),
                "DRAFT",
                request.sourceType().trim(),
                request.sourceId(),
                instructions,
                aiResponse.output(),
                modelName(aiResponse)
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
                output,
                "crm-rules"
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
                output,
                "crm-rules"
        );
    }

    @Override
    public AiInteractionDto assessChurnRisk(AiChurnRiskRequest request) {
        String tenantId = currentTenant();
        Account account = currentAccount(tenantId, request.accountId());
        long activityCount = activityRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());
        long communicationCount = conversationRecordRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());
        long commerceEventCount = commerceEventRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());

        int risk = 55;
        if (activityCount == 0) {
            risk += 15;
        } else {
            risk -= 10;
        }
        if (communicationCount == 0) {
            risk += 15;
        } else {
            risk -= 10;
        }
        if (commerceEventCount == 0) {
            risk += 10;
        } else {
            risk -= 15;
        }
        if (account.getWebsite() == null || account.getWebsite().isBlank()) {
            risk += 5;
        }
        if (account.getIndustry() == null || account.getIndustry().isBlank()) {
            risk += 5;
        }
        risk = Math.max(0, Math.min(risk, 100));

        String level = risk >= 70 ? "HIGH" : risk >= 40 ? "MEDIUM" : "LOW";
        String prompt = "Assess churn risk for account " + account.getName()
                + ", activities=" + activityCount
                + ", communications=" + communicationCount
                + ", commerceEvents=" + commerceEventCount;
        String output = "Account churn risk " + risk + "/100 (" + level + ") for " + account.getName()
                + ". Signals: activities=" + activityCount
                + ", communications=" + communicationCount
                + ", commerceEvents=" + commerceEventCount + ".";

        return saveInteraction(
                request.name().trim(),
                "CHURN_RISK",
                "ACCOUNT",
                account.getId(),
                prompt,
                output,
                "crm-rules"
        );
    }

    @Override
    public AiInteractionDto recommendNextAction(AiRecommendationRequest request) {
        String tenantId = currentTenant();
        String sourceType = request.sourceType().trim().toUpperCase();
        Long sourceId = request.sourceId();
        String objective = normalizeOrDefault(request.objective(), "Increase conversion and retention");

        String recommendation = switch (sourceType) {
            case "LEAD" -> recommendForLead(tenantId, sourceId, objective);
            case "ACCOUNT" -> recommendForAccount(tenantId, sourceId, objective);
            default -> throw new BadRequestException("Unsupported recommendation source type: " + sourceType);
        };

        String output = recommendation;
        if (request.autoCreateActivity()) {
            Activity activity = createRecommendedActivity(sourceType, sourceId, recommendation);
            output = recommendation + " Activity created: " + activity.getSubject() + ".";
        }

        return saveInteraction(
                request.name().trim(),
                "RECOMMENDATION",
                sourceType,
                sourceId,
                "Objective: " + objective,
                output,
                "crm-rules"
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
            String outputText,
            String modelName
    ) {
        AiInteraction interaction = new AiInteraction();
        interaction.setTenantId(currentTenant());
        interaction.setName(name);
        interaction.setOperationType(operationType);
        interaction.setSourceType(sourceType);
        interaction.setSourceId(sourceId);
        interaction.setPromptText(promptText);
        interaction.setOutputText(outputText);
        interaction.setModelName(modelName);
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

    private String recommendForLead(String tenantId, Long leadId, String objective) {
        Lead lead = currentLead(tenantId, leadId);
        long activityCount = activityRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "LEAD", lead.getId());
        long communicationCount = conversationRecordRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "LEAD", lead.getId());

        if (communicationCount == 0) {
            return "Recommended next action: send a first-touch outreach to " + lead.getFullName()
                    + " to support the objective '" + objective + "'.";
        }
        if (activityCount == 0) {
            return "Recommended next action: schedule a qualification call with " + lead.getFullName()
                    + " to advance the objective '" + objective + "'.";
        }
        if ("QUALIFIED".equalsIgnoreCase(lead.getStatus())) {
            return "Recommended next action: prepare a tailored proposal for " + lead.getFullName()
                    + " and align it to the objective '" + objective + "'.";
        }
        return "Recommended next action: confirm timeline and budget with " + lead.getFullName()
                + " to support the objective '" + objective + "'.";
    }

    private String recommendForAccount(String tenantId, Long accountId, String objective) {
        Account account = currentAccount(tenantId, accountId);
        long activityCount = activityRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());
        long communicationCount = conversationRecordRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());
        long commerceEventCount = commerceEventRepository.countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(tenantId, "ACCOUNT", account.getId());

        if (communicationCount == 0) {
            return "Recommended next action: reach out to the primary contact at " + account.getName()
                    + " and reopen engagement around '" + objective + "'.";
        }
        if (commerceEventCount == 0) {
            return "Recommended next action: book an adoption review with " + account.getName()
                    + " to improve retention and the objective '" + objective + "'.";
        }
        if (activityCount < 2) {
            return "Recommended next action: schedule a success check-in for " + account.getName()
                    + " and capture expansion signals for '" + objective + "'.";
        }
        return "Recommended next action: propose an upsell or renewal conversation with " + account.getName()
                + " tied to the objective '" + objective + "'.";
    }

    private Activity createRecommendedActivity(String sourceType, Long sourceId, String recommendation) {
        Activity activity = new Activity();
        activity.setTenantId(currentTenant());
        activity.setType("TASK");
        activity.setSubject("AI recommendation follow-up");
        activity.setRelatedEntityType(sourceType);
        activity.setRelatedEntityId(sourceId);
        activity.setDetails(recommendation);
        activity.setCreatedAt(Instant.now());
        Activity saved = activityRepository.save(activity);
        auditLogService.record(
                "CREATE",
                "ACTIVITY",
                saved.getId(),
                "Created AI recommendation follow-up activity for " + sourceType + " " + sourceId
        );
        return saved;
    }

    private String fallbackSummary(String promptText) {
        String normalized = promptText.replaceAll("\\s+", " ").trim();
        String summary = normalized.length() <= 180
                ? normalized
                : normalized.substring(0, 177) + "...";
        return "Summary: " + summary;
    }

    private String modelName(AiProviderResponse aiResponse) {
        String provider = aiResponse.provider() == null || aiResponse.provider().isBlank()
                ? "provider"
                : aiResponse.provider().trim();
        String model = aiResponse.model() == null || aiResponse.model().isBlank()
                ? "model"
                : aiResponse.model().trim();
        return provider + ":" + model;
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
