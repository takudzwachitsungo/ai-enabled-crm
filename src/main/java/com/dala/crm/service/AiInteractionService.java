package com.dala.crm.service;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiAccountHealthRequest;
import com.dala.crm.dto.AiChurnRiskRequest;
import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.dto.AiLeadScoreRequest;
import com.dala.crm.dto.AiRecommendationRequest;
import com.dala.crm.dto.AiSummarizeRequest;
import java.util.List;

/**
 * Public service contract for aiassistant module use cases.
 */
public interface AiInteractionService {

    /**
     * Generates and records a tenant-scoped summary response.
     */
    AiInteractionDto summarize(AiSummarizeRequest request);

    /**
     * Generates and records a tenant-scoped draft response.
     */
    AiInteractionDto draft(AiDraftRequest request);

    /**
     * Generates and records a tenant-scoped lead score insight.
     */
    AiInteractionDto scoreLead(AiLeadScoreRequest request);

    /**
     * Generates and records a tenant-scoped account health insight.
     */
    AiInteractionDto assessAccountHealth(AiAccountHealthRequest request);

    /**
     * Generates and records a tenant-scoped churn risk insight.
     */
    AiInteractionDto assessChurnRisk(AiChurnRiskRequest request);

    /**
     * Generates and records a tenant-scoped next best action recommendation.
     */
    AiInteractionDto recommendNextAction(AiRecommendationRequest request);

    /**
     * Returns the current tenant scope list for this module.
     */
    List<AiInteractionDto> list();
}
