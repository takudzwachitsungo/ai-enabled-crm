package com.dala.crm.dto;

/**
 * Tenant-scoped SLA lifecycle report for tickets.
 */
public record TicketSlaReportResponse(
        long totalTickets,
        long openTickets,
        long resolvedTickets,
        long overdueTickets,
        long escalatedTickets,
        long unassignedTickets,
        long resolvedWithinSlaTickets,
        long breachedTickets
) {
}
