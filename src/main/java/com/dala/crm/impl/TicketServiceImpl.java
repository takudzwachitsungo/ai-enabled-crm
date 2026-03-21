package com.dala.crm.impl;

import com.dala.crm.dto.TicketAssignmentUpdateRequest;
import com.dala.crm.dto.TicketCreateRequest;
import com.dala.crm.dto.TicketEscalationRunResponse;
import com.dala.crm.dto.TicketResponse;
import com.dala.crm.dto.TicketSlaReportResponse;
import com.dala.crm.dto.TicketStatusUpdateRequest;
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
    private static final String RESOLVED_STATUS = "RESOLVED";
    private static final String ESCALATED_STATUS = "ESCALATED";

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
        SlaPolicy activePolicy = activePolicy(tenantId, priority);
        ticket.setAssignee(resolveAssignee(request.assignee(), activePolicy));
        ticket.setSourceChannel(trimToNull(request.sourceChannel()));
        ticket.setRelatedEntityType(trimToNull(request.relatedEntityType()));
        ticket.setRelatedEntityId(request.relatedEntityId());
        ticket.setDueAt(resolveDueAt(activePolicy, now));
        ticket.setCreatedAt(now);

        Ticket savedTicket = ticketRepository.save(ticket);
        auditLogService.record("CREATE", "TICKET", savedTicket.getId(), "Created ticket " + savedTicket.getTitle());
        recordTimelineActivity(savedTicket, now);
        return toResponse(savedTicket);
    }

    @Override
    public TicketResponse updateStatus(Long id, TicketStatusUpdateRequest request) {
        Ticket ticket = currentTicket(id);
        String status = normalizeStatus(request.status());
        ticket.setStatus(status);
        ticket.setResolvedAt(RESOLVED_STATUS.equals(status) ? Instant.now() : null);
        Ticket savedTicket = ticketRepository.save(ticket);
        auditLogService.record("UPDATE_STATUS", "TICKET", savedTicket.getId(), "Updated ticket status to " + status);
        recordChangeActivity(savedTicket, "Ticket status changed to " + status, trimToNull(request.note()));
        return toResponse(savedTicket);
    }

    @Override
    public TicketResponse updateAssignment(Long id, TicketAssignmentUpdateRequest request) {
        Ticket ticket = currentTicket(id);
        String assignee = request.assignee().trim();
        ticket.setAssignee(assignee);
        Ticket savedTicket = ticketRepository.save(ticket);
        auditLogService.record("ASSIGN", "TICKET", savedTicket.getId(), "Assigned ticket to " + assignee);
        recordChangeActivity(savedTicket, "Ticket assigned to " + assignee, trimToNull(request.note()));
        return toResponse(savedTicket);
    }

    @Override
    public TicketEscalationRunResponse runEscalations() {
        String tenantId = currentTenant();
        Instant now = Instant.now();
        List<Ticket> overdueTickets = ticketRepository
                .findByTenantIdAndStatusNotAndDueAtBeforeAndEscalatedAtIsNullOrderByDueAtAsc(tenantId, RESOLVED_STATUS, now);

        for (Ticket ticket : overdueTickets) {
            ticket.setStatus(ESCALATED_STATUS);
            ticket.setEscalatedAt(now);
            ticketRepository.save(ticket);
            auditLogService.record("ESCALATE", "TICKET", ticket.getId(), "Escalated overdue ticket " + ticket.getTitle());
            recordChangeActivity(ticket, "Ticket escalated after SLA breach", "Ticket breached its due time at " + ticket.getDueAt());
        }

        return new TicketEscalationRunResponse(overdueTickets.size());
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
    public TicketSlaReportResponse getSlaReport() {
        String tenantId = currentTenant();
        Instant now = Instant.now();
        List<Ticket> tickets = ticketRepository.findByTenantId(tenantId);

        long resolvedWithinSlaTickets = tickets.stream()
                .filter(ticket -> ticket.getResolvedAt() != null)
                .filter(ticket -> ticket.getDueAt() != null)
                .filter(ticket -> !ticket.getResolvedAt().isAfter(ticket.getDueAt()))
                .count();

        long breachedTickets = tickets.stream()
                .filter(ticket -> ticket.getDueAt() != null)
                .filter(ticket -> (ticket.getResolvedAt() != null && ticket.getResolvedAt().isAfter(ticket.getDueAt()))
                        || (ticket.getResolvedAt() == null && now.isAfter(ticket.getDueAt())))
                .count();

        return new TicketSlaReportResponse(
                ticketRepository.countByTenantId(tenantId),
                ticketRepository.countByTenantIdAndStatus(tenantId, OPEN_STATUS),
                ticketRepository.countByTenantIdAndStatus(tenantId, RESOLVED_STATUS),
                ticketRepository.countByTenantIdAndStatusNotAndDueAtBefore(tenantId, RESOLVED_STATUS, now),
                ticketRepository.countByTenantIdAndStatus(tenantId, ESCALATED_STATUS),
                ticketRepository.countByTenantIdAndAssigneeIsNull(tenantId),
                resolvedWithinSlaTickets,
                breachedTickets
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long id) {
        return toResponse(currentTicket(id));
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

    private void recordChangeActivity(Ticket ticket, String subject, String details) {
        Activity activity = new Activity();
        activity.setTenantId(ticket.getTenantId());
        activity.setType("TICKET");
        activity.setSubject(subject);
        activity.setRelatedEntityType("TICKET");
        activity.setRelatedEntityId(ticket.getId());
        activity.setDetails(details);
        activity.setCreatedAt(Instant.now());
        activityRepository.save(activity);
    }

    private SlaPolicy activePolicy(String tenantId, String priority) {
        return slaPolicyRepository.findFirstByTenantIdAndActiveTrueAndPriority(tenantId, priority)
                .orElse(null);
    }

    private Instant resolveDueAt(SlaPolicy policy, Instant createdAt) {
        return java.util.Optional.ofNullable(policy)
                .map(SlaPolicy::getResponseHours)
                .map(hours -> createdAt.plusSeconds(hours.longValue() * 3600L))
                .orElse(null);
    }

    private String resolveAssignee(String requestedAssignee, SlaPolicy policy) {
        String explicitAssignee = trimToNull(requestedAssignee);
        if (explicitAssignee != null) {
            return explicitAssignee;
        }
        return policy == null ? null : trimToNull(policy.getDefaultAssignee());
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalizePriority(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeStatus(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private Ticket currentTicket(Long id) {
        String tenantId = currentTenant();
        return ticketRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new TicketNotFoundException(id));
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
                ticket.getEscalatedAt(),
                ticket.getResolvedAt(),
                ticket.getCreatedAt()
        );
    }
}
