package com.dala.crm.service;

import com.dala.crm.dto.ReportSnapshotDto;
import java.util.List;

/**
 * Public service contract for reporting module use cases.
 */
public interface ReportSnapshotService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<ReportSnapshotDto> list();
}
