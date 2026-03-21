package com.dala.crm.service;

import com.dala.crm.dto.TicketCreateRequest;
import com.dala.crm.dto.TicketResponse;
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
     * Returns tickets for the current tenant.
     */
    List<TicketResponse> getTickets();

    /**
     * Returns one ticket in current tenant scope.
     */
    TicketResponse getTicket(Long id);
}
