package com.dala.crm.service;

import com.dala.crm.dto.CommerceStatusUpdateRequest;
import com.dala.crm.dto.InvoiceCreateRequest;
import com.dala.crm.dto.InvoiceResponse;
import com.dala.crm.dto.RenewalAutomationRunResponse;
import java.util.List;

/**
 * Service contract for invoice management.
 */
public interface InvoiceService {

    InvoiceResponse create(InvoiceCreateRequest request);

    RenewalAutomationRunResponse runRenewalAutomation();

    InvoiceResponse updateStatus(Long id, CommerceStatusUpdateRequest request);

    List<InvoiceResponse> list();

    InvoiceResponse get(Long id);
}
