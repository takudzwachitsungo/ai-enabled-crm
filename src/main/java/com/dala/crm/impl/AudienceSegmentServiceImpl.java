package com.dala.crm.impl;

import com.dala.crm.dto.AudienceSegmentCreateRequest;
import com.dala.crm.dto.AudienceSegmentResponse;
import com.dala.crm.entity.AudienceSegment;
import com.dala.crm.exception.AudienceSegmentNotFoundException;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.AudienceSegmentRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.AudienceSegmentService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default audience segment service implementation.
 */
@Service
@Transactional
public class AudienceSegmentServiceImpl implements AudienceSegmentService {

    private final AudienceSegmentRepository audienceSegmentRepository;
    private final AuditLogService auditLogService;

    public AudienceSegmentServiceImpl(
            AudienceSegmentRepository audienceSegmentRepository,
            AuditLogService auditLogService
    ) {
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public AudienceSegmentResponse create(AudienceSegmentCreateRequest request) {
        AudienceSegment segment = new AudienceSegment();
        segment.setTenantId(currentTenant());
        segment.setName(request.name().trim());
        segment.setSourceType(normalize(request.sourceType()));
        segment.setCriteria(request.criteria().trim());
        segment.setEstimatedSize(request.estimatedSize());
        segment.setActive(request.active());
        segment.setCreatedAt(Instant.now());

        AudienceSegment savedSegment = audienceSegmentRepository.save(segment);
        auditLogService.record("CREATE", "AUDIENCE_SEGMENT", savedSegment.getId(), "Created audience segment " + savedSegment.getName());
        return toResponse(savedSegment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AudienceSegmentResponse> list() {
        return audienceSegmentRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AudienceSegmentResponse get(Long id) {
        return toResponse(currentSegment(id));
    }

    private AudienceSegment currentSegment(Long id) {
        String tenantId = currentTenant();
        return audienceSegmentRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new AudienceSegmentNotFoundException(id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private AudienceSegmentResponse toResponse(AudienceSegment segment) {
        return new AudienceSegmentResponse(
                segment.getId(),
                segment.getName(),
                segment.getSourceType(),
                segment.getCriteria(),
                segment.getEstimatedSize(),
                segment.isActive(),
                segment.getCreatedAt()
        );
    }
}
