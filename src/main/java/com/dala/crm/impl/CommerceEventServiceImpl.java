package com.dala.crm.impl;

import com.dala.crm.dto.CommerceEventCreateRequest;
import com.dala.crm.dto.CommerceEventResponse;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.CommerceEvent;
import com.dala.crm.entity.IntegrationConnection;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.CommerceEventNotFoundException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.CommerceEventRepository;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.CommerceEventService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default commerce event service implementation.
 */
@Service
@Transactional
public class CommerceEventServiceImpl implements CommerceEventService {

    private final CommerceEventRepository commerceEventRepository;
    private final IntegrationConnectionRepository integrationConnectionRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public CommerceEventServiceImpl(
            CommerceEventRepository commerceEventRepository,
            IntegrationConnectionRepository integrationConnectionRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.commerceEventRepository = commerceEventRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public CommerceEventResponse create(CommerceEventCreateRequest request) {
        String tenantId = currentTenant();
        IntegrationConnection connection = currentConnection(tenantId, request.integrationConnectionId());
        validateConnectorType(connection);
        Instant now = Instant.now();

        CommerceEvent event = new CommerceEvent();
        event.setTenantId(tenantId);
        event.setIntegrationConnectionId(connection.getId());
        event.setEventType(normalize(request.eventType()));
        event.setSourceReference(request.sourceReference().trim());
        event.setRelatedEntityType(trimToNullUpper(request.relatedEntityType()));
        event.setRelatedEntityId(request.relatedEntityId());
        event.setAmount(request.amount());
        event.setPayload(request.payload().trim());
        event.setCreatedAt(now);

        CommerceEvent savedEvent = commerceEventRepository.save(event);
        auditLogService.record(
                "CREATE",
                "COMMERCE_EVENT",
                savedEvent.getId(),
                "Imported " + savedEvent.getEventType() + " from " + connection.getName()
        );
        recordActivity(savedEvent, connection.getName(), now);
        return toResponse(savedEvent, connection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommerceEventResponse> list() {
        String tenantId = currentTenant();
        return commerceEventRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(event -> toResponse(event, currentConnection(tenantId, event.getIntegrationConnectionId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommerceEventResponse get(Long id) {
        CommerceEvent event = currentEvent(id);
        IntegrationConnection connection = currentConnection(event.getTenantId(), event.getIntegrationConnectionId());
        return toResponse(event, connection);
    }

    private void validateConnectorType(IntegrationConnection connection) {
        String channelType = connection.getChannelType().trim().toUpperCase(Locale.ROOT);
        if (!channelType.equals("POS") && !channelType.equals("ERP")) {
            throw new BadRequestException("Commerce events require a POS or ERP integration");
        }
    }

    private void recordActivity(CommerceEvent event, String integrationName, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(event.getTenantId());
        activity.setType("COMMERCE");
        activity.setSubject("Commerce event imported: " + event.getEventType());
        activity.setRelatedEntityType("COMMERCE_EVENT");
        activity.setRelatedEntityId(event.getId());
        activity.setDetails("Integration: " + integrationName + ", source reference: " + event.getSourceReference());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private CommerceEvent currentEvent(Long id) {
        String tenantId = currentTenant();
        return commerceEventRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new CommerceEventNotFoundException(id));
    }

    private IntegrationConnection currentConnection(String tenantId, Long id) {
        return integrationConnectionRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Integration connection not found: " + id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNullUpper(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private CommerceEventResponse toResponse(CommerceEvent event, IntegrationConnection connection) {
        return new CommerceEventResponse(
                event.getId(),
                event.getIntegrationConnectionId(),
                connection.getName(),
                event.getEventType(),
                event.getSourceReference(),
                event.getRelatedEntityType(),
                event.getRelatedEntityId(),
                event.getAmount(),
                event.getPayload(),
                event.getCreatedAt()
        );
    }
}
