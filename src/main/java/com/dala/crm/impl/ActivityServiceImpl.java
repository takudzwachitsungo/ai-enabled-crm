package com.dala.crm.impl;

import com.dala.crm.dto.ActivityCreateRequest;
import com.dala.crm.dto.ActivityResponse;
import com.dala.crm.entity.Activity;
import com.dala.crm.exception.ActivityNotFoundException;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.ActivityService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default activity service implementation for MVP timeline operations.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public ActivityServiceImpl(ActivityRepository activityRepository, AuditLogService auditLogService) {
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public ActivityResponse createActivity(ActivityCreateRequest request) {
        Activity activity = new Activity();
        activity.setTenantId(currentTenant());
        activity.setType(request.type().trim());
        activity.setSubject(request.subject().trim());
        activity.setRelatedEntityType(request.relatedEntityType().trim());
        activity.setRelatedEntityId(request.relatedEntityId());
        activity.setDetails(trimToNull(request.details()));
        activity.setCreatedAt(Instant.now());
        Activity savedActivity = activityRepository.save(activity);
        auditLogService.record("CREATE", "ACTIVITY", savedActivity.getId(), "Created activity " + savedActivity.getSubject());
        return toResponse(savedActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivities() {
        return activityRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getActivity(Long id) {
        String tenantId = currentTenant();
        Activity activity = activityRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ActivityNotFoundException(id));
        return toResponse(activity);
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

    private ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getType(),
                activity.getSubject(),
                activity.getRelatedEntityType(),
                activity.getRelatedEntityId(),
                activity.getDetails(),
                activity.getCreatedAt()
        );
    }
}
