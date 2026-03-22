package com.dala.crm.controller;

import com.dala.crm.dto.IntegrationConnectionCreateRequest;
import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.dto.IntegrationMarketplaceAppDto;
import com.dala.crm.dto.IntegrationMarketplaceInstallRequest;
import com.dala.crm.service.IntegrationConnectionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for integration connection management.
 */
@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationConnectionController {

    private final IntegrationConnectionService integrationConnectionService;

    public IntegrationConnectionController(IntegrationConnectionService integrationConnectionService) {
        this.integrationConnectionService = integrationConnectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).INTEGRATIONS_WRITE)")
    public IntegrationConnectionDto create(@Valid @RequestBody IntegrationConnectionCreateRequest request) {
        return integrationConnectionService.create(request);
    }

    @GetMapping("/marketplace")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).INTEGRATIONS_READ)")
    public List<IntegrationMarketplaceAppDto> marketplace() {
        return integrationConnectionService.listMarketplaceApps();
    }

    @PostMapping("/marketplace/install")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).INTEGRATIONS_WRITE)")
    public IntegrationConnectionDto install(@Valid @RequestBody IntegrationMarketplaceInstallRequest request) {
        return integrationConnectionService.installMarketplaceApp(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).INTEGRATIONS_READ)")
    public List<IntegrationConnectionDto> list() {
        return integrationConnectionService.list();
    }
}
