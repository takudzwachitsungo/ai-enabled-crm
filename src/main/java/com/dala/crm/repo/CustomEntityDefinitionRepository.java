package com.dala.crm.repo;

import com.dala.crm.entity.CustomEntityDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository access for custom entity definitions.
 */
public interface CustomEntityDefinitionRepository extends JpaRepository<CustomEntityDefinition, Long> {

    List<CustomEntityDefinition> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    Optional<CustomEntityDefinition> findByTenantIdAndApiNameIgnoreCase(String tenantId, String apiName);
}
