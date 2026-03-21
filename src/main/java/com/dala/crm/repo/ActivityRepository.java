package com.dala.crm.repo;

import com.dala.crm.entity.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access contract for activity persistence.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
