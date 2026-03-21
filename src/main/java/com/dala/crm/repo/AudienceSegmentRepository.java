package com.dala.crm.repo;

import com.dala.crm.entity.AudienceSegment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA access for audience segments.
 */
public interface AudienceSegmentRepository extends JpaRepository<AudienceSegment, Long> {

    List<AudienceSegment> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
