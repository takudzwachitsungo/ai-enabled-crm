package com.dala.crm.impl;

import com.dala.crm.dto.IntegrationConnectionCreateRequest;
import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.entity.IntegrationConnection;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.IntegrationConnectionService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default integration service implementation.
 */
@Service
@Transactional
public class IntegrationConnectionServiceImpl implements IntegrationConnectionService {

    private final IntegrationConnectionRepository repository;
    private final AuditLogService auditLogService;

    public IntegrationConnectionServiceImpl(
            IntegrationConnectionRepository repository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @Override
    public IntegrationConnectionDto create(IntegrationConnectionCreateRequest request) {
        IntegrationConnection connection = new IntegrationConnection();
        connection.setTenantId(currentTenant());
        connection.setName(request.name().trim());
        connection.setChannelType(request.channelType().trim());
        connection.setProvider(request.provider().trim());
        connection.setStatus(request.status().trim());
        connection.setCreatedAt(Instant.now());
        IntegrationConnection savedConnection = repository.save(connection);
        auditLogService.record(
                "CREATE",
                "INTEGRATION_CONNECTION",
                savedConnection.getId(),
                "Connected " + savedConnection.getChannelType() + " provider " + savedConnection.getProvider()
        );
        return toDto(savedConnection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationConnectionDto> list() {
        return repository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDto)
                .toList();
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private IntegrationConnectionDto toDto(IntegrationConnection connection) {
        return new IntegrationConnectionDto(
                connection.getId(),
                connection.getName(),
                connection.getChannelType(),
                connection.getProvider(),
                connection.getStatus(),
                connection.getCreatedAt()
        );
    }
}
