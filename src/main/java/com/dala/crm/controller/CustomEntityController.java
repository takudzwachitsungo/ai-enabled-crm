package com.dala.crm.controller;

import com.dala.crm.dto.CustomEntityDefinitionCreateRequest;
import com.dala.crm.dto.CustomEntityDefinitionDto;
import com.dala.crm.dto.CustomEntityDefinitionUpdateRequest;
import com.dala.crm.dto.CustomEntityRecordCreateRequest;
import com.dala.crm.dto.CustomEntityRecordDto;
import com.dala.crm.service.CustomEntityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for tenant-scoped custom entity platform management.
 */
@RestController
@RequestMapping("/api/v1/custom-entities")
public class CustomEntityController {

    private final CustomEntityService customEntityService;

    public CustomEntityController(CustomEntityService customEntityService) {
        this.customEntityService = customEntityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_WRITE)")
    public CustomEntityDefinitionDto createDefinition(@Valid @RequestBody CustomEntityDefinitionCreateRequest request) {
        return customEntityService.createDefinition(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_READ)")
    public List<CustomEntityDefinitionDto> listDefinitions() {
        return customEntityService.listDefinitions();
    }

    @GetMapping("/{definitionId}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_READ)")
    public CustomEntityDefinitionDto getDefinition(@PathVariable Long definitionId) {
        return customEntityService.getDefinition(definitionId);
    }

    @PatchMapping("/{definitionId}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_WRITE)")
    public CustomEntityDefinitionDto updateDefinition(
            @PathVariable Long definitionId,
            @Valid @RequestBody CustomEntityDefinitionUpdateRequest request
    ) {
        return customEntityService.updateDefinition(definitionId, request);
    }

    @PostMapping("/{definitionId}/records")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_WRITE)")
    public CustomEntityRecordDto createRecord(
            @PathVariable Long definitionId,
            @Valid @RequestBody CustomEntityRecordCreateRequest request
    ) {
        return customEntityService.createRecord(definitionId, request);
    }

    @GetMapping("/{definitionId}/records")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_READ)")
    public List<CustomEntityRecordDto> listRecords(@PathVariable Long definitionId) {
        return customEntityService.listRecords(definitionId);
    }

    @GetMapping("/{definitionId}/records/{recordId}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).CUSTOM_ENTITIES_READ)")
    public CustomEntityRecordDto getRecord(@PathVariable Long definitionId, @PathVariable Long recordId) {
        return customEntityService.getRecord(definitionId, recordId);
    }
}
