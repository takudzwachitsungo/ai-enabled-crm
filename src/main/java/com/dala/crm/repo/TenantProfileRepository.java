package com.dala.crm.repo;

import com.dala.crm.entity.TenantProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for TenantProfile records.
 */
public interface TenantProfileRepository extends JpaRepository<TenantProfile, Long> {

    Optional<TenantProfile> findByTenantIdIgnoreCase(String tenantId);

    boolean existsByTenantIdIgnoreCase(String tenantId);
}
