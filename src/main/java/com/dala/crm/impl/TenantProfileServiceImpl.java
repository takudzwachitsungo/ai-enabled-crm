package com.dala.crm.impl;

import com.dala.crm.dto.TenantProfileDto;
import com.dala.crm.dto.TenantProfileUpdateRequest;
import com.dala.crm.entity.TenantProfile;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.NotFoundException;
import com.dala.crm.repo.TenantProfileRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.TenantProfileService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default identitytenancy service implementation.
 */
@Service
@Transactional
public class TenantProfileServiceImpl implements TenantProfileService {

    private final TenantProfileRepository repository;

    public TenantProfileServiceImpl(TenantProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TenantProfileDto> list() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TenantProfileDto getCurrent() {
        return toDto(currentTenantProfile());
    }

    @Override
    public TenantProfileDto updateCurrent(TenantProfileUpdateRequest request) {
        TenantProfile tenantProfile = currentTenantProfile();
        String deploymentModel = normalizeDeploymentModel(request.deploymentModel());
        tenantProfile.setDeploymentModel(deploymentModel);
        tenantProfile.setDeploymentRegion(trimToNull(request.deploymentRegion()));

        if ("DEDICATED".equals(deploymentModel)) {
            tenantProfile.setDedicatedInstanceKey(trimToNull(request.dedicatedInstanceKey()));
            tenantProfile.setDeploymentStatus(
                    tenantProfile.getDedicatedInstanceKey() == null ? "PROVISIONING" : "ACTIVE"
            );
        } else {
            tenantProfile.setDedicatedInstanceKey(null);
            tenantProfile.setDeploymentStatus("ACTIVE");
        }

        tenantProfile.setUpdatedAt(Instant.now());
        return toDto(repository.save(tenantProfile));
    }

    private TenantProfile currentTenantProfile() {
        String tenantId = TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
        return repository.findByTenantIdIgnoreCase(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant profile not found."));
    }

    private TenantProfileDto toDto(TenantProfile record) {
        return new TenantProfileDto(
                record.getId(),
                record.getTenantId(),
                record.getName(),
                defaultIfBlank(record.getDeploymentModel(), "SHARED"),
                defaultIfBlank(record.getDeploymentStatus(), "ACTIVE"),
                record.getDeploymentRegion(),
                record.getDedicatedInstanceKey()
        );
    }

    private String normalizeDeploymentModel(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!"SHARED".equals(normalized) && !"DEDICATED".equals(normalized)) {
            throw new BadRequestException("Unsupported deploymentModel. Use SHARED or DEDICATED.");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String defaultIfBlank(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
