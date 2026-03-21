package com.dala.crm.service;

import com.dala.crm.dto.CannedResponseCreateRequest;
import com.dala.crm.dto.CannedResponseResponse;
import java.util.List;

/**
 * Public contract for canned response management.
 */
public interface CannedResponseService {

    /**
     * Creates a canned response for the current tenant.
     */
    CannedResponseResponse create(CannedResponseCreateRequest request);

    /**
     * Returns canned responses for the current tenant.
     */
    List<CannedResponseResponse> list();

    /**
     * Returns one canned response in current tenant scope.
     */
    CannedResponseResponse get(Long id);
}
