package com.dala.crm.controller;

import com.dala.crm.dto.SlaPolicyCreateRequest;
import com.dala.crm.dto.SlaPolicyResponse;
import com.dala.crm.service.SlaPolicyService;
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
 * REST endpoints for SLA policy management.
 */
@RestController
@RequestMapping("/api/v1/sla-policies")
public class SlaPolicyController {

    private final SlaPolicyService slaPolicyService;

    public SlaPolicyController(SlaPolicyService slaPolicyService) {
        this.slaPolicyService = slaPolicyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).SLA_POLICIES_WRITE)")
    public SlaPolicyResponse createPolicy(@Valid @RequestBody SlaPolicyCreateRequest request) {
        return slaPolicyService.createPolicy(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).SLA_POLICIES_READ)")
    public List<SlaPolicyResponse> getPolicies() {
        return slaPolicyService.getPolicies();
    }
}
