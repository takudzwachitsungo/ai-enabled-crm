package com.dala.crm.service;

import com.dala.crm.dto.ReportSnapshotDto;
import com.dala.crm.dto.ReportSnapshotCreateRequest;
import java.util.List;

/**
 * Public service contract for reporting module use cases.
 */
public interface ReportSnapshotService {

    /**
     * Generates and stores a new tenant-scoped report snapshot.
     */
    ReportSnapshotDto create(ReportSnapshotCreateRequest request);

    /**
     * Returns the current tenant scope list for this module.
     */
    List<ReportSnapshotDto> list();

    /**
     * Returns a single report snapshot for the current tenant.
     */
    ReportSnapshotDto get(Long id);
}
