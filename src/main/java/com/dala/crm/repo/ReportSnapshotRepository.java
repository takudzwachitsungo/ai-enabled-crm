package com.dala.crm.repo;

import com.dala.crm.entity.ReportSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository contract for ReportSnapshot records.
 */
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {
}
