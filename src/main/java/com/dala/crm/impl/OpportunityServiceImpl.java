package com.dala.crm.impl;

import com.dala.crm.dto.OpportunityCreateRequest;
import com.dala.crm.dto.OpportunityResponse;
import com.dala.crm.entity.Opportunity;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.OpportunityNotFoundException;
import com.dala.crm.repo.OpportunityRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.OpportunityService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default opportunity service implementation for MVP CRUD operations.
 */
@Service
@Transactional
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final AuditLogService auditLogService;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository, AuditLogService auditLogService) {
        this.opportunityRepository = opportunityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public OpportunityResponse createOpportunity(OpportunityCreateRequest request) {
        Opportunity opportunity = new Opportunity();
        opportunity.setTenantId(currentTenant());
        opportunity.setName(request.name().trim());
        opportunity.setAccountName(trimToNull(request.accountName()));
        opportunity.setAmount(request.amount());
        opportunity.setStage(request.stage().trim());
        opportunity.setCreatedAt(Instant.now());
        Opportunity savedOpportunity = opportunityRepository.save(opportunity);
        auditLogService.record("CREATE", "OPPORTUNITY", savedOpportunity.getId(), "Created opportunity " + savedOpportunity.getName());
        return toResponse(savedOpportunity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityResponse> getOpportunities() {
        return opportunityRepository.findByTenantId(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunity(Long id) {
        String tenantId = currentTenant();
        Opportunity opportunity = opportunityRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new OpportunityNotFoundException(id));
        return toResponse(opportunity);
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

    private OpportunityResponse toResponse(Opportunity opportunity) {
        return new OpportunityResponse(
                opportunity.getId(),
                opportunity.getName(),
                opportunity.getAccountName(),
                opportunity.getAmount(),
                opportunity.getStage(),
                opportunity.getCreatedAt()
        );
    }
}
