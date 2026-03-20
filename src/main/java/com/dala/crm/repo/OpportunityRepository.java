package com.dala.crm.repo;

import com.dala.crm.entity.Opportunity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for opportunity persistence.
 */
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}
