package com.dala.crm.service;

import com.dala.crm.dto.CampaignCreateRequest;
import com.dala.crm.dto.CampaignResponse;
import java.util.List;

/**
 * Service contract for campaigns.
 */
public interface CampaignService {

    CampaignResponse create(CampaignCreateRequest request);

    List<CampaignResponse> list();

    CampaignResponse get(Long id);
}
