package com.dala.crm.service;

import com.dala.crm.dto.DashboardAnalyticsResponse;
import com.dala.crm.dto.DashboardRevenueForecastResponse;
import com.dala.crm.dto.DashboardSummaryResponse;

/**
 * Public contract for tenant dashboard summary data.
 */
public interface DashboardService {

    /**
     * Returns an aggregate summary for the current tenant.
     */
    DashboardSummaryResponse getSummary();

    /**
     * Returns expanded analytics for the current tenant.
     */
    DashboardAnalyticsResponse getAnalytics();

    /**
     * Returns revenue forecast metrics for the current tenant.
     */
    DashboardRevenueForecastResponse getRevenueForecast();
}
