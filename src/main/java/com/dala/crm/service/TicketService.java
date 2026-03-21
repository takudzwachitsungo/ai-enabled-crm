package com.dala.crm.service;

import com.dala.crm.dto.TicketAssignmentUpdateRequest;
import com.dala.crm.dto.TicketCreateRequest;
import com.dala.crm.dto.TicketEscalationRunResponse;
import com.dala.crm.dto.TicketResponse;
import com.dala.crm.dto.TicketSlaReportResponse;
import com.dala.crm.dto.TicketStatusUpdateRequest;
import java.util.List;

/**
 * Public contract for ticket management use cases.
 */
public interface TicketService {

    /**
     * Creates a ticket for the current tenant.
     */
    TicketResponse createTicket(TicketCreateRequest request);

    /**
     * Updates the status of a ticket in the current tenant.
     */
    TicketResponse updateStatus(Long id, TicketStatusUpdateRequest request);

    /**
     * Updates the assignee of a ticket in the current tenant.
     */
    TicketResponse updateAssignment(Long id, TicketAssignmentUpdateRequest request);

    /**
     * Escalates overdue tickets that have not already been escalated.
     */
    TicketEscalationRunResponse runEscalations();

    /**
     * Returns tickets for the current tenant.
     */
    List<TicketResponse> getTickets();

    /**
     * Returns SLA lifecycle reporting for the current tenant.
     */
    TicketSlaReportResponse getSlaReport();

    /**
     * Returns one ticket in current tenant scope.
     */
    TicketResponse getTicket(Long id);
}
