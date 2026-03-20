package com.dala.crm.impl;

import com.dala.crm.dto.DashboardSummaryResponse;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AccountRepository;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.repo.ContactRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.repo.LeadRepository;
import com.dala.crm.repo.OpportunityRepository;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.DashboardService;
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
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final IntegrationConnectionRepository integrationConnectionRepository;
    private final ConversationRecordRepository conversationRecordRepository;
    private final AiInteractionRepository aiInteractionRepository;

    public DashboardServiceImpl(
            LeadRepository leadRepository,
            ContactRepository contactRepository,
            AccountRepository accountRepository,
            OpportunityRepository opportunityRepository,
            ActivityRepository activityRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            IntegrationConnectionRepository integrationConnectionRepository,
            ConversationRecordRepository conversationRecordRepository,
            AiInteractionRepository aiInteractionRepository
    ) {
        this.leadRepository = leadRepository;
        this.contactRepository = contactRepository;
        this.accountRepository = accountRepository;
        this.opportunityRepository = opportunityRepository;
        this.activityRepository = activityRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
        this.conversationRecordRepository = conversationRecordRepository;
        this.aiInteractionRepository = aiInteractionRepository;
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
                workflowDefinitionRepository.countByTenantIdAndActiveTrue(tenantId),
                integrationConnectionRepository.countByTenantId(tenantId),
                conversationRecordRepository.countByTenantId(tenantId),
                aiInteractionRepository.countByTenantId(tenantId)
        );
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }
}
