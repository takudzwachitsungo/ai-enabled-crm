package com.dala.crm.controller;

import com.dala.crm.dto.CommerceEventCreateRequest;
import com.dala.crm.dto.CommerceEventResponse;
import com.dala.crm.service.CommerceEventService;
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
 * REST endpoints for commerce events imported from connectors.
 */
@RestController
@RequestMapping("/api/v1/commerce-events")
public class CommerceEventController {

    private final CommerceEventService commerceEventService;

    public CommerceEventController(CommerceEventService commerceEventService) {
        this.commerceEventService = commerceEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).COMMERCE_EVENTS_WRITE)")
    public CommerceEventResponse create(@Valid @RequestBody CommerceEventCreateRequest request) {
        return commerceEventService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).COMMERCE_EVENTS_READ)")
    public List<CommerceEventResponse> list() {
        return commerceEventService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).COMMERCE_EVENTS_READ)")
    public CommerceEventResponse get(@PathVariable Long id) {
        return commerceEventService.get(id);
    }
}
