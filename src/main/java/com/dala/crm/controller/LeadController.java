package com.dala.crm.controller;

import com.dala.crm.dto.LeadCreateRequest;
import com.dala.crm.dto.LeadResponse;
import com.dala.crm.service.LeadService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for lead management in the CRM core module.
 */
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadResponse createLead(@Valid @RequestBody LeadCreateRequest request) {
        return leadService.createLead(request);
    }

    @GetMapping
    public List<LeadResponse> getLeads() {
        return leadService.getLeads();
    }

    @GetMapping("/{id}")
    public LeadResponse getLead(@PathVariable Long id) {
        return leadService.getLead(id);
    }
}
