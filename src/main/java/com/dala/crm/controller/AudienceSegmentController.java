package com.dala.crm.controller;

import com.dala.crm.dto.AudienceSegmentCreateRequest;
import com.dala.crm.dto.AudienceSegmentResponse;
import com.dala.crm.service.AudienceSegmentService;
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
 * REST endpoints for audience segment management.
 */
@RestController
@RequestMapping("/api/v1/audience-segments")
public class AudienceSegmentController {

    private final AudienceSegmentService audienceSegmentService;

    public AudienceSegmentController(AudienceSegmentService audienceSegmentService) {
        this.audienceSegmentService = audienceSegmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AUDIENCE_SEGMENTS_WRITE)")
    public AudienceSegmentResponse create(@Valid @RequestBody AudienceSegmentCreateRequest request) {
        return audienceSegmentService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AUDIENCE_SEGMENTS_READ)")
    public List<AudienceSegmentResponse> list() {
        return audienceSegmentService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).AUDIENCE_SEGMENTS_READ)")
    public AudienceSegmentResponse get(@PathVariable Long id) {
        return audienceSegmentService.get(id);
    }
}
