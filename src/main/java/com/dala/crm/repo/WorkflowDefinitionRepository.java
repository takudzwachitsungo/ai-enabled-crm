package com.dala.crm.repo;

import com.dala.crm.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for WorkflowDefinition records.
 */
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
}
