package com.dala.crm.repo;

import com.dala.crm.entity.CommerceEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for commerce events.
 */
public interface CommerceEventRepository extends JpaRepository<CommerceEvent, Long> {

    List<CommerceEvent> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(String tenantId, String relatedEntityType, Long relatedEntityId);
}
