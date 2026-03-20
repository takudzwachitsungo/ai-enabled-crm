package com.dala.crm.repo;

import com.dala.crm.entity.Lead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for lead persistence.
 */
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}
