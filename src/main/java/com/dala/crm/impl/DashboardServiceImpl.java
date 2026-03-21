package com.dala.crm.impl;

import com.dala.crm.dto.DashboardAnalyticsResponse;
import com.dala.crm.dto.DashboardRevenueForecastResponse;
import com.dala.crm.dto.DashboardSummaryResponse;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.repo.AudienceSegmentRepository;
import com.dala.crm.repo.CampaignRepository;
import com.dala.crm.repo.CannedResponseRepository;
import com.dala.crm.repo.ContactRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.repo.KnowledgeBaseArticleRepository;
import com.dala.crm.repo.LeadRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.repo.OpportunityRepository;
import com.dala.crm.repo.QuoteRepository;
import com.dala.crm.repo.ReportSnapshotRepository;
import com.dala.crm.repo.TicketRepository;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.DashboardService;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default dashboard aggregation service.
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final AccountRepository accountRepository;
    private final OpportunityRepository opportunityRepository;
    private final ActivityRepository activityRepository;
    private final TicketRepository ticketRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final IntegrationConnectionRepository integrationConnectionRepository;
    private final ConversationRecordRepository conversationRecordRepository;
    private final AiInteractionRepository aiInteractionRepository;
    private final KnowledgeBaseArticleRepository knowledgeBaseArticleRepository;
    private final CannedResponseRepository cannedResponseRepository;
    private final AudienceSegmentRepository audienceSegmentRepository;
    private final CampaignRepository campaignRepository;
    private final ReportSnapshotRepository reportSnapshotRepository;
    private final QuoteRepository quoteRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardServiceImpl(
            LeadRepository leadRepository,
            ContactRepository contactRepository,
            AccountRepository accountRepository,
            OpportunityRepository opportunityRepository,
            ActivityRepository activityRepository,
            TicketRepository ticketRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            IntegrationConnectionRepository integrationConnectionRepository,
            ConversationRecordRepository conversationRecordRepository,
            AiInteractionRepository aiInteractionRepository,
            KnowledgeBaseArticleRepository knowledgeBaseArticleRepository,
            CannedResponseRepository cannedResponseRepository,
            AudienceSegmentRepository audienceSegmentRepository,
            CampaignRepository campaignRepository,
            ReportSnapshotRepository reportSnapshotRepository,
            QuoteRepository quoteRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.leadRepository = leadRepository;
        this.contactRepository = contactRepository;
        this.accountRepository = accountRepository;
        this.opportunityRepository = opportunityRepository;
        this.activityRepository = activityRepository;
        this.ticketRepository = ticketRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
        this.conversationRecordRepository = conversationRecordRepository;
        this.aiInteractionRepository = aiInteractionRepository;
        this.knowledgeBaseArticleRepository = knowledgeBaseArticleRepository;
        this.cannedResponseRepository = cannedResponseRepository;
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.campaignRepository = campaignRepository;
        this.reportSnapshotRepository = reportSnapshotRepository;
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        String tenantId = currentTenant();
        return new DashboardSummaryResponse(
                leadRepository.countByTenantId(tenantId),
                contactRepository.countByTenantId(tenantId),
                accountRepository.countByTenantId(tenantId),
                opportunityRepository.countByTenantId(tenantId),
                activityRepository.countByTenantId(tenantId),
                ticketRepository.countByTenantId(tenantId),
                ticketRepository.countByTenantIdAndStatusNotAndDueAtBefore(tenantId, "RESOLVED", Instant.now()),
                workflowDefinitionRepository.countByTenantIdAndActiveTrue(tenantId),
                integrationConnectionRepository.countByTenantId(tenantId),
                conversationRecordRepository.countByTenantId(tenantId),
                aiInteractionRepository.countByTenantId(tenantId)
        );
    }

    @Override
    public DashboardAnalyticsResponse getAnalytics() {
        String tenantId = currentTenant();
        return new DashboardAnalyticsResponse(
                knowledgeBaseArticleRepository.countByTenantIdAndPublishedTrue(tenantId),
                cannedResponseRepository.countByTenantId(tenantId),
                audienceSegmentRepository.countByTenantIdAndActiveTrue(tenantId),
                campaignRepository.countByTenantIdAndStatus(tenantId, "DRAFT"),
                campaignRepository.countByTenantIdAndStatus(tenantId, "SCHEDULED"),
                ticketRepository.countByTenantIdAndStatus(tenantId, "OPEN"),
                ticketRepository.countByTenantIdAndStatus(tenantId, "ESCALATED"),
                reportSnapshotRepository.countByTenantId(tenantId)
        );
    }

    @Override
    public DashboardRevenueForecastResponse getRevenueForecast() {
        String tenantId = currentTenant();

        BigDecimal totalPipelineAmount = opportunityRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(opportunity -> safeAmount(opportunity.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal weightedPipelineAmount = opportunityRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(opportunity -> safeAmount(opportunity.getAmount()).multiply(stageWeight(opportunity.getStage())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal activeQuoteAmount = quoteRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(quote -> isActiveQuoteStatus(quote.getStatus()))
                .map(quote -> safeAmount(quote.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal issuedInvoiceAmount = invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(invoice -> "ISSUED".equalsIgnoreCase(invoice.getStatus()))
                .map(invoice -> safeAmount(invoice.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal collectedInvoiceAmount = invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(invoice -> "PAID".equalsIgnoreCase(invoice.getStatus()))
                .map(invoice -> safeAmount(invoice.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal projectedRevenueAmount = weightedPipelineAmount
                .add(activeQuoteAmount.multiply(new BigDecimal("0.60")))
                .add(issuedInvoiceAmount)
                .add(collectedInvoiceAmount);

        return new DashboardRevenueForecastResponse(
                totalPipelineAmount,
                weightedPipelineAmount,
                activeQuoteAmount,
                issuedInvoiceAmount,
                collectedInvoiceAmount,
                projectedRevenueAmount,
                Instant.now()
        );
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private BigDecimal stageWeight(String stage) {
        if (stage == null) {
            return new BigDecimal("0.25");
        }
        return switch (stage.trim().toUpperCase()) {
            case "NEW" -> new BigDecimal("0.20");
            case "QUALIFIED" -> new BigDecimal("0.40");
            case "PROPOSAL" -> new BigDecimal("0.65");
            case "NEGOTIATION" -> new BigDecimal("0.80");
            case "WON" -> BigDecimal.ONE;
            default -> new BigDecimal("0.25");
        };
    }

    private boolean isActiveQuoteStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toUpperCase();
        return "DRAFT".equals(normalized) || "SENT".equals(normalized) || "APPROVED".equals(normalized);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
