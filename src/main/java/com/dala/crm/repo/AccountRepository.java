package com.dala.crm.repo;

import com.dala.crm.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for account persistence.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}
