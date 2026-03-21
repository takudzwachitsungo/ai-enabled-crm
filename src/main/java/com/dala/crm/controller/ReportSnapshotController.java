package com.dala.crm.controller;

import com.dala.crm.dto.ReportSnapshotCreateRequest;
import com.dala.crm.dto.ReportSnapshotDto;
import com.dala.crm.service.ReportSnapshotService;
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
 * REST endpoints for scheduled report snapshots.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportSnapshotController {

    private final ReportSnapshotService reportSnapshotService;

    public ReportSnapshotController(ReportSnapshotService reportSnapshotService) {
        this.reportSnapshotService = reportSnapshotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).REPORTS_WRITE)")
    public ReportSnapshotDto create(@Valid @RequestBody ReportSnapshotCreateRequest request) {
        return reportSnapshotService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).REPORTS_READ)")
    public List<ReportSnapshotDto> list() {
        return reportSnapshotService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).REPORTS_READ)")
    public ReportSnapshotDto get(@PathVariable Long id) {
        return reportSnapshotService.get(id);
    }
}
