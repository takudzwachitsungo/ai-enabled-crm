package com.dala.crm.controller;

import com.dala.crm.dto.DashboardAnalyticsResponse;
import com.dala.crm.dto.DashboardSummaryResponse;
import com.dala.crm.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard endpoints for aggregate tenant metrics.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).DASHBOARD_READ)")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).DASHBOARD_READ)")
    public DashboardAnalyticsResponse getAnalytics() {
        return dashboardService.getAnalytics();
    }
}
