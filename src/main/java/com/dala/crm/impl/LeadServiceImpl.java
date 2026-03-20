package com.dala.crm.impl;

import com.dala.crm.exception.BadRequestException;
import com.dala.crm.security.TenantContext;
import com.dala.crm.dto.LeadCreateRequest;
import com.dala.crm.dto.LeadResponse;
import com.dala.crm.entity.Lead;
import com.dala.crm.exception.LeadNotFoundException;
import com.dala.crm.repo.LeadRepository;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.LeadService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default lead service implementation for MVP CRUD operations.
 */
@Service
@Transactional
public class LeadServiceImpl implements LeadService {

    private static final String NEW_STATUS = "NEW";
    private final LeadRepository leadRepository;
    private final AuditLogService auditLogService;

    public LeadServiceImpl(LeadRepository leadRepository, AuditLogService auditLogService) {
        this.leadRepository = leadRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public LeadResponse createLead(LeadCreateRequest request) {
        String tenantId = currentTenant();

        Lead lead = new Lead();
        lead.setTenantId(tenantId);
        lead.setFullName(request.fullName().trim());
        lead.setEmail(request.email().trim().toLowerCase());
        lead.setStatus(NEW_STATUS);
        lead.setCreatedAt(Instant.now());

        Lead savedLead = leadRepository.save(lead);
        auditLogService.record("CREATE", "LEAD", savedLead.getId(), "Created lead " + savedLead.getFullName());
        return toResponse(savedLead);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponse> getLeads() {
        String tenantId = currentTenant();
        return leadRepository.findByTenantId(tenantId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponse getLead(Long id) {
        String tenantId = currentTenant();
        Lead lead = leadRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new LeadNotFoundException(id));

        return toResponse(lead);
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getFullName(),
                lead.getEmail(),
                lead.getStatus(),
                lead.getCreatedAt()
        );
    }
}
