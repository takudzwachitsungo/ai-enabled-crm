package com.dala.crm.controller;

import com.dala.crm.dto.CannedResponseCreateRequest;
import com.dala.crm.dto.CannedResponseResponse;
import com.dala.crm.service.CannedResponseService;
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
 * REST endpoints for canned response management.
 */
@RestController
@RequestMapping("/api/v1/canned-responses")
public class CannedResponseController {

    private final CannedResponseService cannedResponseService;

    public CannedResponseController(CannedResponseService cannedResponseService) {
        this.cannedResponseService = cannedResponseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CANNED_RESPONSES_WRITE)")
    public CannedResponseResponse create(@Valid @RequestBody CannedResponseCreateRequest request) {
        return cannedResponseService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CANNED_RESPONSES_READ)")
    public List<CannedResponseResponse> list() {
        return cannedResponseService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CANNED_RESPONSES_READ)")
    public CannedResponseResponse get(@PathVariable Long id) {
        return cannedResponseService.get(id);
    }
}
