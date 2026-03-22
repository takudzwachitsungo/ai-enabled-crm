package com.dala.crm.controller;

import com.dala.crm.dto.AppUserCreateRequest;
import com.dala.crm.dto.AppUserPasswordResetRequest;
import com.dala.crm.dto.AppUserResponse;
import com.dala.crm.dto.AppUserUpdateRequest;
import com.dala.crm.security.CrmAuthorities;
import com.dala.crm.service.AppUserAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tenant-scoped user administration endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
public class AppUserAdminController {

    private final AppUserAdminService appUserAdminService;

    public AppUserAdminController(AppUserAdminService appUserAdminService) {
        this.appUserAdminService = appUserAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).USERS_READ)")
    public List<AppUserResponse> list() {
        return appUserAdminService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).USERS_WRITE)")
    public AppUserResponse create(@Valid @RequestBody AppUserCreateRequest request) {
        return appUserAdminService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).USERS_WRITE)")
    public AppUserResponse update(@PathVariable Long id, @Valid @RequestBody AppUserUpdateRequest request) {
        return appUserAdminService.update(id, request);
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).USERS_WRITE)")
    public AppUserResponse resetPassword(@PathVariable Long id, @Valid @RequestBody AppUserPasswordResetRequest request) {
        return appUserAdminService.resetPassword(id, request);
    }
}
