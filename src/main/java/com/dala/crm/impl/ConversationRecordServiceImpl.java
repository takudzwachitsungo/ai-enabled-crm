package com.dala.crm.impl;

import com.dala.crm.dto.ConversationRecordCreateRequest;
import com.dala.crm.dto.ConversationRecordDto;
import com.dala.crm.entity.ConversationRecord;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.ConversationRecordService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default communication service implementation.
 */
@Service
@Transactional
public class ConversationRecordServiceImpl implements ConversationRecordService {

    private final ConversationRecordRepository repository;
    private final AuditLogService auditLogService;

    public ConversationRecordServiceImpl(
            ConversationRecordRepository repository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @Override
    public ConversationRecordDto create(ConversationRecordCreateRequest request) {
        ConversationRecord conversationRecord = new ConversationRecord();
        conversationRecord.setTenantId(currentTenant());
        conversationRecord.setName(request.name().trim());
        conversationRecord.setChannelType(request.channelType().trim());
        conversationRecord.setDirection(request.direction().trim());
        conversationRecord.setParticipant(request.participant().trim());
        conversationRecord.setSubject(trimToNull(request.subject()));
        conversationRecord.setMessageBody(request.messageBody().trim());
        conversationRecord.setRelatedEntityType(trimToNull(request.relatedEntityType()));
        conversationRecord.setRelatedEntityId(request.relatedEntityId());
        conversationRecord.setCreatedAt(Instant.now());
        ConversationRecord savedRecord = repository.save(conversationRecord);
        auditLogService.record(
                "CREATE",
                "CONVERSATION_RECORD",
                savedRecord.getId(),
                "Logged " + savedRecord.getChannelType() + " conversation with " + savedRecord.getParticipant()
        );
        return toDto(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationRecordDto> list() {
        return repository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDto)
                .toList();
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

    private ConversationRecordDto toDto(ConversationRecord record) {
        return new ConversationRecordDto(
                record.getId(),
                record.getName(),
                record.getChannelType(),
                record.getDirection(),
                record.getParticipant(),
                record.getSubject(),
                record.getMessageBody(),
                record.getRelatedEntityType(),
                record.getRelatedEntityId(),
                record.getCreatedAt()
        );
    }
}
