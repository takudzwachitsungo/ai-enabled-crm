package com.dala.crm.repo;

import com.dala.crm.entity.AiInteraction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for AiInteraction records.
 */
public interface AiInteractionRepository extends JpaRepository<AiInteraction, Long> {

    List<AiInteraction> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantId(String tenantId);
}
