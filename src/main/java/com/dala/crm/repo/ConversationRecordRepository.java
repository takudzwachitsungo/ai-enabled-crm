package com.dala.crm.repo;

import com.dala.crm.entity.ConversationRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for ConversationRecord records.
 */
public interface ConversationRecordRepository extends JpaRepository<ConversationRecord, Long> {

    List<ConversationRecord> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantIdAndRelatedEntityTypeAndRelatedEntityId(String tenantId, String relatedEntityType, Long relatedEntityId);

    long countByTenantId(String tenantId);
}
