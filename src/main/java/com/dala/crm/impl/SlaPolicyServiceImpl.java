package com.dala.crm.impl;

import com.dala.crm.dto.SlaPolicyCreateRequest;
import com.dala.crm.dto.SlaPolicyResponse;
import com.dala.crm.entity.SlaPolicy;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.SlaPolicyRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.SlaPolicyService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default SLA policy management service.
 */
@Service
@Transactional
public class SlaPolicyServiceImpl implements SlaPolicyService {

    private final SlaPolicyRepository slaPolicyRepository;
    private final AuditLogService auditLogService;

    public SlaPolicyServiceImpl(SlaPolicyRepository slaPolicyRepository, AuditLogService auditLogService) {
        this.slaPolicyRepository = slaPolicyRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public SlaPolicyResponse createPolicy(SlaPolicyCreateRequest request) {
        SlaPolicy slaPolicy = new SlaPolicy();
        slaPolicy.setTenantId(currentTenant());
        slaPolicy.setName(request.name().trim());
        slaPolicy.setPriority(normalizePriority(request.priority()));
        slaPolicy.setResponseHours(request.responseHours());
        slaPolicy.setDefaultAssignee(trimToNull(request.defaultAssignee()));
        slaPolicy.setActive(request.active());
        slaPolicy.setCreatedAt(Instant.now());
        SlaPolicy savedPolicy = slaPolicyRepository.save(slaPolicy);
        auditLogService.record(
                "CREATE",
                "SLA_POLICY",
                savedPolicy.getId(),
                "Created SLA policy " + savedPolicy.getName() + " for " + savedPolicy.getPriority() + " priority"
        );
        return toResponse(savedPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlaPolicyResponse> getPolicies() {
        return slaPolicyRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalizePriority(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private SlaPolicyResponse toResponse(SlaPolicy slaPolicy) {
        return new SlaPolicyResponse(
                slaPolicy.getId(),
                slaPolicy.getName(),
                slaPolicy.getPriority(),
                slaPolicy.getResponseHours(),
                slaPolicy.getDefaultAssignee(),
                slaPolicy.isActive(),
                slaPolicy.getCreatedAt()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
