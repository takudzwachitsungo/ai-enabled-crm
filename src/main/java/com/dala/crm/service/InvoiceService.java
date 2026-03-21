package com.dala.crm.service;

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

    List<InvoiceResponse> list();

    InvoiceResponse get(Long id);
}
