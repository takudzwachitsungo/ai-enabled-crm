package com.dala.crm.controller;

import com.dala.crm.dto.TicketAssignmentUpdateRequest;
import com.dala.crm.dto.TicketCreateRequest;
import com.dala.crm.dto.TicketResponse;
import com.dala.crm.dto.TicketStatusUpdateRequest;
import com.dala.crm.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for tenant-scoped service tickets.
 */
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TICKETS_WRITE)")
    public TicketResponse createTicket(@Valid @RequestBody TicketCreateRequest request) {
        return ticketService.createTicket(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TICKETS_WRITE)")
    public TicketResponse updateStatus(@PathVariable Long id, @Valid @RequestBody TicketStatusUpdateRequest request) {
        return ticketService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/assignment")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TICKETS_WRITE)")
    public TicketResponse updateAssignment(@PathVariable Long id, @Valid @RequestBody TicketAssignmentUpdateRequest request) {
        return ticketService.updateAssignment(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TICKETS_READ)")
    public List<TicketResponse> getTickets() {
        return ticketService.getTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).TICKETS_READ)")
    public TicketResponse getTicket(@PathVariable Long id) {
        return ticketService.getTicket(id);
    }
}
