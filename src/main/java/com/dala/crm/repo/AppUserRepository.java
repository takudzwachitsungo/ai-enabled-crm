package com.dala.crm.repo;

import com.dala.crm.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for tenant-scoped application users.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByTenantIdAndEmailIgnoreCase(String tenantId, String email);

    boolean existsByTenantIdAndEmailIgnoreCase(String tenantId, String email);

    java.util.List<AppUser> findByTenantIdOrderByCreatedAtAsc(String tenantId);
}
