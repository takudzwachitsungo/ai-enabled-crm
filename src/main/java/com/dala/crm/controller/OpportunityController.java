package com.dala.crm.controller;

import com.dala.crm.dto.OpportunityCreateRequest;
import com.dala.crm.dto.OpportunityResponse;
import com.dala.crm.service.OpportunityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for opportunity management in the CRM core module.
 */
@RestController
@RequestMapping("/api/v1/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).OPPORTUNITIES_WRITE)")
    public OpportunityResponse createOpportunity(@Valid @RequestBody OpportunityCreateRequest request) {
        return opportunityService.createOpportunity(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).OPPORTUNITIES_READ)")
    public List<OpportunityResponse> getOpportunities() {
        return opportunityService.getOpportunities();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).OPPORTUNITIES_READ)")
    public OpportunityResponse getOpportunity(@PathVariable Long id) {
        return opportunityService.getOpportunity(id);
    }
}
