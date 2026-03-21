package com.dala.crm.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Revenue forecast payload derived from pipeline and commerce signals.
 */
public record DashboardRevenueForecastResponse(
        BigDecimal totalPipelineAmount,
        BigDecimal weightedPipelineAmount,
        BigDecimal activeQuoteAmount,
        BigDecimal issuedInvoiceAmount,
        BigDecimal collectedInvoiceAmount,
        BigDecimal projectedRevenueAmount,
        Instant generatedAt
) {
}
