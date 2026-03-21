package com.dala.crm.service;

import com.dala.crm.dto.QuoteCreateRequest;
import com.dala.crm.dto.QuoteResponse;
import java.util.List;

/**
 * Service contract for quote management.
 */
public interface QuoteService {

    QuoteResponse create(QuoteCreateRequest request);

    List<QuoteResponse> list();

    QuoteResponse get(Long id);
}
