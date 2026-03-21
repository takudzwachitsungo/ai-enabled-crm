package com.dala.crm.service;

import com.dala.crm.dto.CommerceEventCreateRequest;
import com.dala.crm.dto.CommerceEventResponse;
import java.util.List;

/**
 * Service contract for commerce events imported from connectors.
 */
public interface CommerceEventService {

    CommerceEventResponse create(CommerceEventCreateRequest request);

    List<CommerceEventResponse> list();

    CommerceEventResponse get(Long id);
}
