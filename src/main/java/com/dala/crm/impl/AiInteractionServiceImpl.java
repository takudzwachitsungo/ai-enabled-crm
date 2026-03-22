package com.dala.crm.impl;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiChatMessageRequest;
import com.dala.crm.dto.AiChatRequest;
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
import com.dala.crm.entity.TenantProfile;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.CampaignRepository;
import com.dala.crm.repo.CommerceEventRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.repo.LeadRepository;
import com.dala.crm.repo.OpportunityRepository;
import com.dala.crm.repo.QuoteRepository;
import com.dala.crm.repo.ReportSnapshotRepository;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.repo.TenantProfileRepository;
import com.dala.crm.repo.TicketRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.AiGatewayClient;
import com.dala.crm.service.AiInteractionService;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final OpportunityRepository opportunityRepository;
    private final TicketRepository ticketRepository;
    private final ActivityRepository activityRepository;
    private final ConversationRecordRepository conversationRecordRepository;
    private final CommerceEventRepository commerceEventRepository;
    private final QuoteRepository quoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final CampaignRepository campaignRepository;
    private final ReportSnapshotRepository reportSnapshotRepository;
    private final IntegrationConnectionRepository integrationConnectionRepository;
    private final TenantProfileRepository tenantProfileRepository;
    private final AiGatewayClient aiGatewayClient;
    private final AuditLogService auditLogService;

    public AiInteractionServiceImpl(
            AiInteractionRepository repository,
            LeadRepository leadRepository,
            AccountRepository accountRepository,
            OpportunityRepository opportunityRepository,
            TicketRepository ticketRepository,
            ActivityRepository activityRepository,
            ConversationRecordRepository conversationRecordRepository,
            CommerceEventRepository commerceEventRepository,
            QuoteRepository quoteRepository,
            InvoiceRepository invoiceRepository,
            CampaignRepository campaignRepository,
            ReportSnapshotRepository reportSnapshotRepository,
            IntegrationConnectionRepository integrationConnectionRepository,
            TenantProfileRepository tenantProfileRepository,
            AiGatewayClient aiGatewayClient,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.accountRepository = accountRepository;
        this.opportunityRepository = opportunityRepository;
        this.ticketRepository = ticketRepository;
        this.activityRepository = activityRepository;
        this.conversationRecordRepository = conversationRecordRepository;
        this.commerceEventRepository = commerceEventRepository;
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.campaignRepository = campaignRepository;
        this.reportSnapshotRepository = reportSnapshotRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
        this.tenantProfileRepository = tenantProfileRepository;
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
    public AiInteractionDto chat(AiChatRequest request) {
        String tenantId = currentTenant();
        String tenantName = tenantProfileRepository.findByTenantIdIgnoreCase(tenantId)
                .map(TenantProfile::getName)
                .orElse(tenantId);
        String question = request.message().trim();
        String assistantName = normalizeOrDefault(request.name(), "Workspace assistant");
        String companyContext = buildWorkspaceContext(tenantId, tenantName);
        List<Map<String, String>> conversation = Optional.ofNullable(request.conversation())
                .orElse(List.of())
                .stream()
                .limit(10)
                .map(this::toConversationTurn)
                .toList();

        AiProviderResponse aiResponse = aiGatewayClient.chat(
                tenantId,
                tenantName,
                companyContext,
                conversation,
                question
        ).orElseGet(() -> new AiProviderResponse(
                "spring-fallback",
                "workspace-chat",
                fallbackChatResponse(tenantName, companyContext, question)
        ));

        return saveInteraction(
                assistantName,
                "CHAT",
                "WORKSPACE",
                null,
                question,
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

    private String buildWorkspaceContext(String tenantId, String tenantName) {
        List<com.dala.crm.entity.Lead> leads = leadRepository.findByTenantId(tenantId);
        List<com.dala.crm.entity.Account> accounts = accountRepository.findByTenantId(tenantId);
        List<com.dala.crm.entity.Opportunity> opportunities = opportunityRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.Ticket> tickets = ticketRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.Activity> activities = activityRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.ConversationRecord> communications =
                conversationRecordRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.Quote> quotes = quoteRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.Invoice> invoices = invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.Campaign> campaigns = campaignRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.ReportSnapshot> reports =
                reportSnapshotRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        List<com.dala.crm.entity.IntegrationConnection> integrations =
                integrationConnectionRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);

        StringBuilder builder = new StringBuilder();
        builder.append("Workspace: ").append(tenantName).append(" (").append(tenantId).append(")\n");
        builder.append("Summary counts: ")
                .append("leads=").append(leads.size())
                .append(", accounts=").append(accounts.size())
                .append(", opportunities=").append(opportunities.size())
                .append(", tickets=").append(tickets.size())
                .append(", activities=").append(activities.size())
                .append(", communications=").append(communications.size())
                .append(", quotes=").append(quotes.size())
                .append(", invoices=").append(invoices.size())
                .append(", campaigns=").append(campaigns.size())
                .append(", reports=").append(reports.size())
                .append(", integrations=").append(integrations.size())
                .append(", aiInteractions=").append(repository.countByTenantId(tenantId))
                .append(".\n");
        builder.append("Open ticket count: ")
                .append(ticketRepository.countByTenantIdAndStatus(tenantId, "OPEN"))
                .append(". Overdue unresolved ticket count: ")
                .append(ticketRepository.countByTenantIdAndStatusNotAndDueAtBefore(tenantId, "RESOLVED", Instant.now()))
                .append(".\n");

        appendSection(
                builder,
                "Recent leads",
                leads.stream()
                        .sorted(Comparator.comparing(com.dala.crm.entity.Lead::getCreatedAt).reversed())
                        .limit(4)
                        .map(lead -> lead.getFullName() + " [" + lead.getStatus() + "] <" + lead.getEmail() + ">")
                        .toList()
        );
        appendSection(
                builder,
                "Recent opportunities",
                opportunities.stream()
                        .limit(4)
                        .map(opportunity -> opportunity.getName() + " [" + opportunity.getStage() + "] amount="
                                + opportunity.getAmount() + " account=" + normalizeOrDefault(opportunity.getAccountName(), "n/a"))
                        .toList()
        );
        appendSection(
                builder,
                "Current tickets",
                tickets.stream()
                        .limit(4)
                        .map(ticket -> ticket.getTitle() + " [" + ticket.getStatus() + "/" + ticket.getPriority()
                                + "] assignee=" + normalizeOrDefault(ticket.getAssignee(), "unassigned"))
                        .toList()
        );
        appendSection(
                builder,
                "Quote book",
                quotes.stream()
                        .limit(3)
                        .map(quote -> quote.getName() + " [" + quote.getStatus() + "] amount=" + quote.getAmount())
                        .toList()
        );
        appendSection(
                builder,
                "Invoice book",
                invoices.stream()
                        .limit(3)
                        .map(invoice -> invoice.getInvoiceNumber() + " [" + invoice.getStatus() + "] amount=" + invoice.getAmount())
                        .toList()
        );
        appendSection(
                builder,
                "Campaigns",
                campaigns.stream()
                        .limit(3)
                        .map(campaign -> campaign.getName() + " [" + campaign.getStatus() + "] delivered="
                                + campaign.getDeliveredCount())
                        .toList()
        );
        appendSection(
                builder,
                "Recent reports",
                reports.stream()
                        .limit(3)
                        .map(report -> report.getName() + " [" + report.getReportType() + "/" + report.getStatus() + "]")
                        .toList()
        );
        appendSection(
                builder,
                "Connected integrations",
                integrations.stream()
                        .limit(3)
                        .map(integration -> integration.getName() + " [" + integration.getChannelType() + "/" + integration.getStatus() + "]")
                        .toList()
        );
        return builder.toString().trim();
    }

    private void appendSection(StringBuilder builder, String heading, List<String> items) {
        builder.append(heading).append(":\n");
        if (items.isEmpty()) {
            builder.append("- none\n");
            return;
        }
        for (String item : items) {
            builder.append("- ").append(item).append("\n");
        }
    }

    private Map<String, String> toConversationTurn(AiChatMessageRequest message) {
        return Map.of(
                "role", normalizeConversationRole(message.role()),
                "content", message.content().trim()
        );
    }

    private String normalizeConversationRole(String value) {
        String normalized = normalizeOrDefault(value, "user").toLowerCase();
        return switch (normalized) {
            case "assistant", "system" -> normalized;
            default -> "user";
        };
    }

    private String fallbackChatResponse(String tenantName, String companyContext, String question) {
        String compactContext = companyContext.length() <= 900
                ? companyContext
                : companyContext.substring(0, 897) + "...";
        return "I couldn't reach the dedicated AI service, so here is a local workspace answer for "
                + tenantName + ". Question: " + question + "\n\nWorkspace snapshot:\n" + compactContext;
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
