package com.dala.crm.service;

import com.dala.crm.dto.InvoiceCreateRequest;
import com.dala.crm.dto.InvoiceResponse;
import java.util.List;

/**
 * Service contract for invoice management.
 */
public interface InvoiceService {

    InvoiceResponse create(InvoiceCreateRequest request);

    List<InvoiceResponse> list();

    InvoiceResponse get(Long id);
}
