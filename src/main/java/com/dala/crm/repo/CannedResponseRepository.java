package com.dala.crm.repo;

import com.dala.crm.entity.CannedResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for canned responses.
 */
public interface CannedResponseRepository extends JpaRepository<CannedResponse, Long> {

    List<CannedResponse> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantId(String tenantId);
}
