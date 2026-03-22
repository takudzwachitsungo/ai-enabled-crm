package com.dala.crm.impl;

import com.dala.crm.dto.IntegrationConnectionCreateRequest;
import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.dto.IntegrationMarketplaceAppDto;
import com.dala.crm.dto.IntegrationMarketplaceInstallRequest;
import com.dala.crm.dto.IntegrationConnectionUpdateRequest;
import com.dala.crm.entity.IntegrationConnection;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.IntegrationConnectionException;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.IntegrationConnectionService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default integration service implementation.
 */
@Service
@Transactional
public class IntegrationConnectionServiceImpl implements IntegrationConnectionService {

    private static final Set<String> SUPPORTED_CONNECTION_STATUSES = Set.of(
            "CONNECTED",
            "DISCONNECTED",
            "ERROR",
            "ARCHIVED",
            "PROVISIONING"
    );

    private static final Map<String, IntegrationMarketplaceAppDto> MARKETPLACE_APPS = Map.of(
            "whatsapp-cloud", new IntegrationMarketplaceAppDto(
                    "whatsapp-cloud",
                    "WhatsApp Cloud",
                    "MESSAGING",
                    "WHATSAPP",
                    "META",
                    "1.0.0",
                    "AVAILABLE",
                    "Deliver tenant-scoped WhatsApp conversations through Meta Cloud APIs.",
                    List.of("OUTBOUND_MESSAGES", "INBOUND_WEBHOOKS", "CONVERSATION_SYNC")
            ),
            "gmail-sync", new IntegrationMarketplaceAppDto(
                    "gmail-sync",
                    "Gmail Sync",
                    "EMAIL",
                    "EMAIL",
                    "GOOGLE",
                    "1.0.0",
                    "AVAILABLE",
                    "Sync mailbox activity into CRM communications and workflow triggers.",
                    List.of("EMAIL_CAPTURE", "THREAD_SYNC", "WORKFLOW_TRIGGER")
            ),
            "retail-pos", new IntegrationMarketplaceAppDto(
                    "retail-pos",
                    "Retail POS",
                    "COMMERCE",
                    "POS",
                    "INTERNAL",
                    "1.0.0",
                    "AVAILABLE",
                    "Ingest point-of-sale commerce events and link them back to CRM records.",
                    List.of("SALE_EVENTS", "PAYMENT_SYNC", "ACCOUNT_LINKING")
            )
    );

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
        connection.setMarketplaceAppKey(null);
        connection.setMarketplaceVersion(null);
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
    public List<IntegrationMarketplaceAppDto> listMarketplaceApps() {
        return MARKETPLACE_APPS.values().stream()
                .sorted((left, right) -> left.name().compareToIgnoreCase(right.name()))
                .toList();
    }

    @Override
    public IntegrationConnectionDto installMarketplaceApp(IntegrationMarketplaceInstallRequest request) {
        String tenantId = currentTenant();
        String appKey = request.appKey().trim().toLowerCase(Locale.ROOT);
        IntegrationMarketplaceAppDto app = MARKETPLACE_APPS.get(appKey);
        if (app == null) {
            throw new BadRequestException("Unknown marketplace app: " + request.appKey().trim());
        }

        IntegrationConnection connection = new IntegrationConnection();
        connection.setTenantId(tenantId);
        connection.setName(Objects.requireNonNullElse(trimToNull(request.connectionName()), app.name()));
        connection.setChannelType(app.channelType());
        connection.setProvider(app.provider());
        connection.setMarketplaceAppKey(app.appKey());
        connection.setMarketplaceVersion(app.version());
        connection.setStatus("CONNECTED");
        connection.setCreatedAt(Instant.now());
        IntegrationConnection savedConnection = repository.save(connection);
        auditLogService.record(
                "INSTALL",
                "INTEGRATION_MARKETPLACE_APP",
                savedConnection.getId(),
                "Installed marketplace app " + app.name()
        );
        return toDto(savedConnection);
    }

    @Override
    public IntegrationConnectionDto update(Long id, IntegrationConnectionUpdateRequest request) {
        IntegrationConnection connection = currentConnection(id);
        connection.setName(request.name().trim());
        connection.setStatus(normalizeStatus(request.status()));

        IntegrationConnection savedConnection = repository.save(connection);
        auditLogService.record(
                "UPDATE",
                "INTEGRATION_CONNECTION",
                savedConnection.getId(),
                "Updated integration connection " + savedConnection.getName()
        );
        return toDto(savedConnection);
    }

    @Override
    public IntegrationConnectionDto uninstall(Long id) {
        IntegrationConnection connection = currentConnection(id);
        connection.setStatus("DISCONNECTED");

        IntegrationConnection savedConnection = repository.save(connection);
        auditLogService.record(
                "UNINSTALL",
                "INTEGRATION_CONNECTION",
                savedConnection.getId(),
                "Uninstalled integration connection " + savedConnection.getName()
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

    private IntegrationConnection currentConnection(Long id) {
        String tenantId = currentTenant();
        return repository.findById(id)
                .filter(record -> tenantId.equals(record.getTenantId()))
                .orElseThrow(() -> new IntegrationConnectionException("Integration connection not found: " + id));
    }

    private String normalizeStatus(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_CONNECTION_STATUSES.contains(normalized)) {
            throw new BadRequestException("Unsupported integration status: " + value.trim());
        }
        return normalized;
    }

    private IntegrationConnectionDto toDto(IntegrationConnection connection) {
        return new IntegrationConnectionDto(
                connection.getId(),
                connection.getName(),
                connection.getChannelType(),
                connection.getProvider(),
                connection.getMarketplaceAppKey(),
                connection.getMarketplaceVersion(),
                connection.getStatus(),
                connection.getCreatedAt()
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
