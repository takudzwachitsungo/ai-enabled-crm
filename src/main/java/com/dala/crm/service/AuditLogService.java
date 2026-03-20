package com.dala.crm.service;

import com.dala.crm.dto.AuditLogResponse;
import java.util.List;

/**
 * Public contract for reading tenant audit history and recording system actions.
 */
public interface AuditLogService {

    /**
     * Returns tenant-scoped audit records ordered newest first.
     */
    List<AuditLogResponse> getAuditLogs();

    /**
     * Records a tenant-scoped action performed by the current actor.
     */
    void record(String action, String entityType, Long entityId, String summary);
}
