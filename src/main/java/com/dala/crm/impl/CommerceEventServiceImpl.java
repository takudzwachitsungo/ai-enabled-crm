package com.dala.crm.impl;

import com.dala.crm.dto.CommerceEventCreateRequest;
import com.dala.crm.dto.CommerceEventResponse;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.CommerceEvent;
import com.dala.crm.entity.ConversationRecord;
import com.dala.crm.entity.IntegrationConnection;
import com.dala.crm.entity.Invoice;
import com.dala.crm.entity.Quote;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.CommerceEventNotFoundException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.CommerceEventRepository;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.repo.InvoiceRepository;
import com.dala.crm.repo.QuoteRepository;
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
    private final QuoteRepository quoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final ConversationRecordRepository conversationRecordRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public CommerceEventServiceImpl(
            CommerceEventRepository commerceEventRepository,
            IntegrationConnectionRepository integrationConnectionRepository,
            QuoteRepository quoteRepository,
            InvoiceRepository invoiceRepository,
            ConversationRecordRepository conversationRecordRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.commerceEventRepository = commerceEventRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.conversationRecordRepository = conversationRecordRepository;
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
        processLifecycleSync(savedEvent, connection, now);
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

    private void processLifecycleSync(CommerceEvent event, IntegrationConnection connection, Instant now) {
        String relatedEntityType = event.getRelatedEntityType();
        Long relatedEntityId = event.getRelatedEntityId();
        if (relatedEntityType == null || relatedEntityId == null) {
            return;
        }

        String eventType = event.getEventType();
        if ("QUOTE".equals(relatedEntityType) && "QUOTE_ACCEPTED".equals(eventType)) {
            Quote quote = currentQuote(event.getTenantId(), relatedEntityId);
            quote.setStatus("APPROVED");
            quoteRepository.save(quote);
            auditLogService.record("SYNC", "QUOTE", quote.getId(), "Synced quote approval from commerce event " + event.getSourceReference());
            recordSyncActivity(event.getTenantId(), "QUOTE", quote.getId(), "Quote approved from connector", "Source reference: " + event.getSourceReference(), now);
            return;
        }

        if ("INVOICE".equals(relatedEntityType) && "PAYMENT_RECEIVED".equals(eventType)) {
            Invoice invoice = currentInvoice(event.getTenantId(), relatedEntityId);
            invoice.setStatus("PAID");
            invoiceRepository.save(invoice);
            auditLogService.record("SYNC", "INVOICE", invoice.getId(), "Synced invoice payment from commerce event " + event.getSourceReference());
            recordSyncActivity(event.getTenantId(), "INVOICE", invoice.getId(), "Invoice paid from connector", "Source reference: " + event.getSourceReference(), now);
            return;
        }

        if ("ACCOUNT".equals(relatedEntityType) && "SALE_COMPLETED".equals(eventType)) {
            ConversationRecord record = new ConversationRecord();
            record.setTenantId(event.getTenantId());
            record.setName("Commerce sync: " + event.getEventType());
            record.setChannelType(connection.getChannelType());
            record.setDirection("INBOUND");
            record.setParticipant(connection.getName());
            record.setSubject("Commerce lifecycle update");
            record.setMessageBody("Imported " + event.getEventType() + " from " + connection.getName() + " with reference " + event.getSourceReference() + ".");
            record.setRelatedEntityType("ACCOUNT");
            record.setRelatedEntityId(relatedEntityId);
            record.setCreatedAt(now);
            ConversationRecord savedRecord = conversationRecordRepository.save(record);
            auditLogService.record("SYNC", "COMMUNICATION", savedRecord.getId(), "Recorded commerce lifecycle communication for account " + relatedEntityId);
            recordSyncActivity(event.getTenantId(), "ACCOUNT", relatedEntityId, "Commerce sale synced to account", "Source reference: " + event.getSourceReference(), now);
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

    private void recordSyncActivity(String tenantId, String relatedEntityType, Long relatedEntityId, String subject, String details, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setType("COMMERCE_SYNC");
        activity.setSubject(subject);
        activity.setRelatedEntityType(relatedEntityType);
        activity.setRelatedEntityId(relatedEntityId);
        activity.setDetails(details);
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

    private Quote currentQuote(String tenantId, Long id) {
        return quoteRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Quote not found: " + id));
    }

    private Invoice currentInvoice(String tenantId, Long id) {
        return invoiceRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Invoice not found: " + id));
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
