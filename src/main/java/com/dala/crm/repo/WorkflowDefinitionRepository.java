package com.dala.crm.repo;

import com.dala.crm.entity.WorkflowDefinition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for WorkflowDefinition records.
 */
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {

    List<WorkflowDefinition> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<WorkflowDefinition> findByTenantIdAndActiveTrueAndTriggerType(String tenantId, String triggerType);

    long countByTenantIdAndActiveTrue(String tenantId);
}
