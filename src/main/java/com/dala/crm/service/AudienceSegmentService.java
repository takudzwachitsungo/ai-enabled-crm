package com.dala.crm.service;

import com.dala.crm.dto.AudienceSegmentCreateRequest;
import com.dala.crm.dto.AudienceSegmentResponse;
import java.util.List;

/**
 * Service contract for audience segments.
 */
public interface AudienceSegmentService {

    AudienceSegmentResponse create(AudienceSegmentCreateRequest request);

    List<AudienceSegmentResponse> list();

    AudienceSegmentResponse get(Long id);
}
