package com.dala.crm.repo;

import com.dala.crm.entity.ConversationRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for ConversationRecord records.
 */
public interface ConversationRecordRepository extends JpaRepository<ConversationRecord, Long> {

    List<ConversationRecord> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantId(String tenantId);
}
