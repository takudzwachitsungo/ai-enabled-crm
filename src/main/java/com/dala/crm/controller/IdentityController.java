package com.dala.crm.controller;

import com.dala.crm.dto.CurrentUserResponse;
import java.util.Comparator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight identity endpoint for validating authentication and authority grants.
 */
@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {

    @GetMapping("/me")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).IDENTITY_READ)")
    public CurrentUserResponse currentUser(Authentication authentication) {
        return new CurrentUserResponse(
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .sorted(Comparator.naturalOrder())
                        .toList()
        );
    }
}
