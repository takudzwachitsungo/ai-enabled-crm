package com.dala.crm.controller;

import com.dala.crm.dto.TenantProfileDto;
import com.dala.crm.dto.TenantProfileUpdateRequest;
import com.dala.crm.service.TenantProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tenant profile and deployment settings endpoints.
 */
@RestController
@RequestMapping("/api/v1/tenant-profile")
public class TenantProfileController {

    private final TenantProfileService tenantProfileService;

    public TenantProfileController(TenantProfileService tenantProfileService) {
        this.tenantProfileService = tenantProfileService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TENANT_PROFILE_READ)")
    public TenantProfileDto getCurrent() {
        return tenantProfileService.getCurrent();
    }

    @PatchMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TENANT_PROFILE_WRITE)")
    public TenantProfileDto updateCurrent(@Valid @RequestBody TenantProfileUpdateRequest request) {
        return tenantProfileService.updateCurrent(request);
    }
}
