package com.dala.crm.repo;

import com.dala.crm.entity.SlaPolicy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for SLA policy persistence.
 */
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {

    List<SlaPolicy> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    Optional<SlaPolicy> findFirstByTenantIdAndActiveTrueAndPriority(String tenantId, String priority);
}
