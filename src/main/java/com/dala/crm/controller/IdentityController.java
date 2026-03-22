package com.dala.crm.controller;

import com.dala.crm.dto.CurrentUserResponse;
import com.dala.crm.dto.PasswordChangeRequest;
import com.dala.crm.dto.SimpleMessageResponse;
import com.dala.crm.security.TenantUserPrincipal;
import com.dala.crm.service.AuthService;
import java.util.Comparator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight identity endpoint for validating authentication and authority grants.
 */
@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {

    private final AuthService authService;

    public IdentityController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).IDENTITY_READ)")
    public CurrentUserResponse currentUser(Authentication authentication) {
        String tenantId = null;
        String tenantName = null;
        String fullName = authentication.getName();
        String email = authentication.getName();

        if (authentication.getPrincipal() instanceof TenantUserPrincipal principal) {
            tenantId = principal.getTenantId();
            tenantName = principal.getTenantName();
            fullName = principal.getFullName();
            email = principal.getEmail();
        }

        return new CurrentUserResponse(
                tenantId,
                tenantName,
                fullName,
                email,
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .sorted(Comparator.naturalOrder())
                        .toList()
        );
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).IDENTITY_READ)")
    public SimpleMessageResponse changePassword(
            Authentication authentication,
            @RequestBody PasswordChangeRequest request
    ) {
        return authService.changePassword(authentication, request);
    }
}
