package com.dala.crm.impl;

import com.dala.crm.dto.AuthLoginRequest;
import com.dala.crm.dto.AuthSessionResponse;
import com.dala.crm.dto.AuthSignupRequest;
import com.dala.crm.dto.PasswordChangeRequest;
import com.dala.crm.dto.SimpleMessageResponse;
import com.dala.crm.entity.AppUser;
import com.dala.crm.entity.TenantProfile;
import com.dala.crm.exception.AuthLoginException;
import com.dala.crm.exception.AuthRegistrationException;
import com.dala.crm.repo.AppUserRepository;
import com.dala.crm.repo.TenantProfileRepository;
import com.dala.crm.security.TenantUserPrincipal;
import com.dala.crm.security.TenantAwareUserDetailsService;
import com.dala.crm.service.AuthService;
import java.time.Instant;
import java.util.Comparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default auth service for local tenant-aware registration and login.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final TenantProfileRepository tenantProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantAwareUserDetailsService tenantAwareUserDetailsService;

    public AuthServiceImpl(
            AppUserRepository appUserRepository,
            TenantProfileRepository tenantProfileRepository,
            PasswordEncoder passwordEncoder,
            TenantAwareUserDetailsService tenantAwareUserDetailsService
    ) {
        this.appUserRepository = appUserRepository;
        this.tenantProfileRepository = tenantProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantAwareUserDetailsService = tenantAwareUserDetailsService;
    }

    @Override
    public AuthSessionResponse register(AuthSignupRequest request) {
        String tenantId = normalizeTenantId(request.tenantId());
        if (tenantProfileRepository.existsByTenantIdIgnoreCase(tenantId)) {
            throw new AuthRegistrationException("Workspace identifier is already in use.");
        }
        if (appUserRepository.existsByTenantIdAndEmailIgnoreCase(tenantId, request.email())) {
            throw new AuthRegistrationException("An account already exists for this email in the selected workspace.");
        }

        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setTenantId(tenantId);
        tenantProfile.setName(request.companyName().trim());
        tenantProfile.setDeploymentModel("SHARED");
        tenantProfile.setDeploymentStatus("ACTIVE");
        tenantProfile.setUpdatedAt(Instant.now());
        tenantProfileRepository.save(tenantProfile);

        AppUser appUser = new AppUser();
        appUser.setTenantId(tenantId);
        appUser.setEmail(request.email().trim().toLowerCase());
        appUser.setFullName(request.fullName().trim());
        appUser.setPasswordHash(passwordEncoder.encode(request.password()));
        appUser.setRole("ADMIN");
        appUser.setActive(true);
        appUserRepository.save(appUser);

        return new AuthSessionResponse(
                tenantId,
                tenantProfile.getName(),
                appUser.getFullName(),
                appUser.getEmail(),
                tenantAwareUserDetailsService.authoritiesForRole(appUser.getRole()).stream()
                        .map(authority -> authority.getAuthority())
                        .sorted(Comparator.naturalOrder())
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthSessionResponse login(AuthLoginRequest request) {
        String tenantId = normalizeTenantId(request.tenantId());
        AppUser appUser = appUserRepository.findByTenantIdAndEmailIgnoreCase(tenantId, request.email().trim())
                .filter(AppUser::isActive)
                .orElseThrow(() -> new AuthLoginException("Invalid workspace credentials."));

        if (!passwordEncoder.matches(request.password(), appUser.getPasswordHash())) {
            throw new AuthLoginException("Invalid workspace credentials.");
        }

        TenantProfile tenantProfile = tenantProfileRepository.findByTenantIdIgnoreCase(tenantId)
                .orElseThrow(() -> new AuthLoginException("Workspace could not be found."));

        return new AuthSessionResponse(
                tenantId,
                tenantProfile.getName(),
                appUser.getFullName(),
                appUser.getEmail(),
                tenantAwareUserDetailsService.authoritiesForRole(appUser.getRole()).stream()
                        .map(authority -> authority.getAuthority())
                        .sorted(Comparator.naturalOrder())
                        .toList()
        );
    }

    @Override
    public SimpleMessageResponse changePassword(Authentication authentication, PasswordChangeRequest request) {
        if (!(authentication.getPrincipal() instanceof TenantUserPrincipal principal)) {
            throw new AuthLoginException("Authenticated user context is unavailable.");
        }

        AppUser appUser = appUserRepository.findByTenantIdAndEmailIgnoreCase(principal.getTenantId(), principal.getEmail())
                .filter(AppUser::isActive)
                .orElseThrow(() -> new AuthLoginException("Authenticated user could not be found."));

        if (!passwordEncoder.matches(request.currentPassword(), appUser.getPasswordHash())) {
            throw new AuthLoginException("Current password is incorrect.");
        }

        appUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        appUserRepository.save(appUser);
        return new SimpleMessageResponse("Password updated successfully.");
    }

    private String normalizeTenantId(String value) {
        return value.trim().toLowerCase();
    }
}
