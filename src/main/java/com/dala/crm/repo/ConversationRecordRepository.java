package com.dala.crm.repo;

import com.dala.crm.entity.ConversationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for ConversationRecord records.
 */
public interface ConversationRecordRepository extends JpaRepository<ConversationRecord, Long> {
}
