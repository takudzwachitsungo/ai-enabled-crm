package com.dala.crm.repo;

import com.dala.crm.entity.IntegrationConnection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for IntegrationConnection records.
 */
public interface IntegrationConnectionRepository extends JpaRepository<IntegrationConnection, Long> {
}
