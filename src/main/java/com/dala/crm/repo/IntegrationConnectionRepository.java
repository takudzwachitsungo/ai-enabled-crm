package com.dala.crm.repo;

import com.dala.crm.entity.IntegrationConnection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for IntegrationConnection records.
 */
public interface IntegrationConnectionRepository extends JpaRepository<IntegrationConnection, Long> {

    List<IntegrationConnection> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantId(String tenantId);
}
