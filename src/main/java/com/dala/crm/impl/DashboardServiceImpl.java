package com.dala.crm.impl;

import com.dala.crm.dto.DashboardAnalyticsResponse;
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
import com.dala.crm.repo.OpportunityRepository;
import com.dala.crm.repo.ReportSnapshotRepository;
import com.dala.crm.repo.TicketRepository;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.DashboardService;
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
            ReportSnapshotRepository reportSnapshotRepository
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

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }
}
