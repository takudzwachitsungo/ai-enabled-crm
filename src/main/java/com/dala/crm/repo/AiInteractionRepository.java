package com.dala.crm.repo;

import com.dala.crm.entity.AiInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for AiInteraction records.
 */
public interface AiInteractionRepository extends JpaRepository<AiInteraction, Long> {
}
