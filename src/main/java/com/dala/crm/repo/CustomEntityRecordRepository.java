package com.dala.crm.repo;

import com.dala.crm.entity.CustomEntityRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository access for custom entity records.
 */
public interface CustomEntityRecordRepository extends JpaRepository<CustomEntityRecord, Long> {

    List<CustomEntityRecord> findByTenantIdAndDefinitionIdOrderByCreatedAtDesc(String tenantId, Long definitionId);
}
