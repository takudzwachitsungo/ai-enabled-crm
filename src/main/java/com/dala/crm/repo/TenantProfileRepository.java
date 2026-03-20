package com.dala.crm.repo;

import com.dala.crm.entity.TenantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for TenantProfile records.
 */
public interface TenantProfileRepository extends JpaRepository<TenantProfile, Long> {
}
