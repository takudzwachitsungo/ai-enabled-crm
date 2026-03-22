package com.dala.crm.impl;

import com.dala.crm.dto.AppUserCreateRequest;
import com.dala.crm.dto.AppUserPasswordResetRequest;
import com.dala.crm.dto.AppUserResponse;
import com.dala.crm.dto.AppUserUpdateRequest;
import com.dala.crm.entity.AppUser;
import com.dala.crm.exception.AuthRegistrationException;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.NotFoundException;
import com.dala.crm.repo.AppUserRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AppUserAdminService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default tenant-scoped user administration service.
 */
@Service
@Transactional
public class AppUserAdminServiceImpl implements AppUserAdminService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserAdminServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUserResponse> list() {
        return appUserRepository.findByTenantIdOrderByCreatedAtAsc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppUserResponse create(AppUserCreateRequest request) {
        String tenantId = currentTenant();
        String role = normalizeRole(request.role());

        if (appUserRepository.existsByTenantIdAndEmailIgnoreCase(tenantId, request.email().trim())) {
            throw new AuthRegistrationException("A workspace user with this email already exists.");
        }

        AppUser user = new AppUser();
        user.setTenantId(tenantId);
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setActive(true);

        return toResponse(appUserRepository.save(user));
    }

    @Override
    public AppUserResponse update(Long id, AppUserUpdateRequest request) {
        String tenantId = currentTenant();
        AppUser user = appUserRepository.findById(id)
                .filter(record -> tenantId.equalsIgnoreCase(record.getTenantId()))
                .orElseThrow(() -> new NotFoundException("Workspace user not found."));

        user.setRole(normalizeRole(request.role()));
        user.setActive(request.active());
        return toResponse(appUserRepository.save(user));
    }

    @Override
    public AppUserResponse resetPassword(Long id, AppUserPasswordResetRequest request) {
        String tenantId = currentTenant();
        AppUser user = appUserRepository.findById(id)
                .filter(record -> tenantId.equalsIgnoreCase(record.getTenantId()))
                .orElseThrow(() -> new NotFoundException("Workspace user not found."));

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return toResponse(appUserRepository.save(user));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase();
        if (!"ADMIN".equals(normalized) && !"VIEWER".equals(normalized)) {
            throw new BadRequestException("Unsupported user role. Use ADMIN or VIEWER.");
        }
        return normalized;
    }

    private AppUserResponse toResponse(AppUser user) {
        return new AppUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
