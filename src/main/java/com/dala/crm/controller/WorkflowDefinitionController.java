package com.dala.crm.controller;

import com.dala.crm.dto.WorkflowDefinitionCreateRequest;
import com.dala.crm.dto.WorkflowBuilderCatalogResponse;
import com.dala.crm.dto.WorkflowDefinitionDto;
import com.dala.crm.service.WorkflowDefinitionService;
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
 * REST endpoints for workflow definition management.
 */
@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowDefinitionController {

    private final WorkflowDefinitionService workflowDefinitionService;

    public WorkflowDefinitionController(WorkflowDefinitionService workflowDefinitionService) {
        this.workflowDefinitionService = workflowDefinitionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).WORKFLOWS_WRITE)")
    public WorkflowDefinitionDto create(@Valid @RequestBody WorkflowDefinitionCreateRequest request) {
        return workflowDefinitionService.create(request);
    }

    @GetMapping("/builder/catalog")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).WORKFLOWS_READ)")
    public WorkflowBuilderCatalogResponse builderCatalog() {
        return workflowDefinitionService.getBuilderCatalog();
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).WORKFLOWS_READ)")
    public List<WorkflowDefinitionDto> list() {
        return workflowDefinitionService.list();
    }
}
