package com.dala.crm.repo;

import com.dala.crm.entity.Campaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for campaigns.
 */
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    long countByTenantIdAndStatus(String tenantId, String status);
}
