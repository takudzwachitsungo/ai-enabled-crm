package com.dala.crm.impl;

import com.dala.crm.dto.AuditLogResponse;
import com.dala.crm.entity.AuditLog;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AuditLogRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default audit log service for recording and reading tenant actions.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void record(String action, String entityType, Long entityId, String summary) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTenantId(currentTenant());
        auditLog.setActor(currentActor());
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setSummary(summary);
        auditLog.setCreatedAt(Instant.now());
        auditLogRepository.save(auditLog);
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "system";
        }
        return authentication.getName();
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getActor(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getSummary(),
                auditLog.getCreatedAt()
        );
    }
}
