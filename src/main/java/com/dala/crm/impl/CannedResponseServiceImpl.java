package com.dala.crm.impl;

import com.dala.crm.dto.CannedResponseCreateRequest;
import com.dala.crm.dto.CannedResponseResponse;
import com.dala.crm.entity.CannedResponse;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.CannedResponseNotFoundException;
import com.dala.crm.repo.CannedResponseRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.CannedResponseService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default canned response service implementation.
 */
@Service
@Transactional
public class CannedResponseServiceImpl implements CannedResponseService {

    private final CannedResponseRepository cannedResponseRepository;
    private final AuditLogService auditLogService;

    public CannedResponseServiceImpl(
            CannedResponseRepository cannedResponseRepository,
            AuditLogService auditLogService
    ) {
        this.cannedResponseRepository = cannedResponseRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public CannedResponseResponse create(CannedResponseCreateRequest request) {
        CannedResponse cannedResponse = new CannedResponse();
        cannedResponse.setTenantId(currentTenant());
        cannedResponse.setTitle(request.title().trim());
        cannedResponse.setChannelType(request.channelType().trim());
        cannedResponse.setCategory(trimToNull(request.category()));
        cannedResponse.setBody(request.body().trim());
        cannedResponse.setCreatedAt(Instant.now());
        CannedResponse savedResponse = cannedResponseRepository.save(cannedResponse);
        auditLogService.record("CREATE", "CANNED_RESPONSE", savedResponse.getId(), "Created canned response " + savedResponse.getTitle());
        return toResponse(savedResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CannedResponseResponse> list() {
        return cannedResponseRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CannedResponseResponse get(Long id) {
        return toResponse(currentResponse(id));
    }

    private CannedResponse currentResponse(Long id) {
        String tenantId = currentTenant();
        return cannedResponseRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new CannedResponseNotFoundException(id));
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

    private CannedResponseResponse toResponse(CannedResponse cannedResponse) {
        return new CannedResponseResponse(
                cannedResponse.getId(),
                cannedResponse.getTitle(),
                cannedResponse.getChannelType(),
                cannedResponse.getCategory(),
                cannedResponse.getBody(),
                cannedResponse.getCreatedAt()
        );
    }
}
