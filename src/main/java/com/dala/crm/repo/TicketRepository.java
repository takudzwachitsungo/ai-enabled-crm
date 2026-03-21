package com.dala.crm.repo;

import com.dala.crm.entity.Ticket;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for service tickets.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<Ticket> findByTenantIdAndStatusNotAndDueAtBeforeAndEscalatedAtIsNullOrderByDueAtAsc(
            String tenantId,
            String status,
            Instant dueAt
    );

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, String status);

    long countByTenantIdAndAssigneeIsNull(String tenantId);

    long countByTenantIdAndStatusNotAndDueAtBefore(String tenantId, String status, Instant dueAt);

    List<Ticket> findByTenantId(String tenantId);
}
