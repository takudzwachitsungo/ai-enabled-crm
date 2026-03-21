package com.dala.crm.impl;

import com.dala.crm.dto.TicketCreateRequest;
import com.dala.crm.dto.TicketResponse;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.SlaPolicy;
import com.dala.crm.entity.Ticket;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.TicketNotFoundException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.SlaPolicyRepository;
import com.dala.crm.repo.TicketRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.TicketService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default ticket service for Phase 2 service operations.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final String OPEN_STATUS = "OPEN";

    private final TicketRepository ticketRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            SlaPolicyRepository slaPolicyRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.ticketRepository = ticketRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public TicketResponse createTicket(TicketCreateRequest request) {
        String tenantId = currentTenant();
        Instant now = Instant.now();
        String priority = normalizePriority(request.priority());

        Ticket ticket = new Ticket();
        ticket.setTenantId(tenantId);
        ticket.setTitle(request.title().trim());
        ticket.setDescription(trimToNull(request.description()));
        ticket.setPriority(priority);
        ticket.setStatus(OPEN_STATUS);
        ticket.setAssignee(trimToNull(request.assignee()));
        ticket.setSourceChannel(trimToNull(request.sourceChannel()));
        ticket.setRelatedEntityType(trimToNull(request.relatedEntityType()));
        ticket.setRelatedEntityId(request.relatedEntityId());
        ticket.setDueAt(resolveDueAt(tenantId, priority, now));
        ticket.setCreatedAt(now);

        Ticket savedTicket = ticketRepository.save(ticket);
        auditLogService.record("CREATE", "TICKET", savedTicket.getId(), "Created ticket " + savedTicket.getTitle());
        recordTimelineActivity(savedTicket, now);
        return toResponse(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTickets() {
        return ticketRepository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long id) {
        String tenantId = currentTenant();
        Ticket ticket = ticketRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new TicketNotFoundException(id));
        return toResponse(ticket);
    }

    private void recordTimelineActivity(Ticket ticket, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(ticket.getTenantId());
        activity.setType("TICKET");
        activity.setSubject("Ticket opened: " + ticket.getTitle());
        activity.setRelatedEntityType("TICKET");
        activity.setRelatedEntityId(ticket.getId());
        activity.setDetails(ticket.getDueAt() == null
                ? "Ticket created without an active SLA policy."
                : "Ticket due at " + ticket.getDueAt());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private Instant resolveDueAt(String tenantId, String priority, Instant createdAt) {
        return slaPolicyRepository.findFirstByTenantIdAndActiveTrueAndPriority(tenantId, priority)
                .map(SlaPolicy::getResponseHours)
                .map(hours -> createdAt.plusSeconds(hours.longValue() * 3600L))
                .orElse(null);
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalizePriority(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getAssignee(),
                ticket.getSourceChannel(),
                ticket.getRelatedEntityType(),
                ticket.getRelatedEntityId(),
                ticket.getDueAt(),
                ticket.getCreatedAt()
        );
    }
}
