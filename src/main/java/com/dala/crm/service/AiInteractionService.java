package com.dala.crm.service;

import com.dala.crm.dto.AiDraftRequest;
import com.dala.crm.dto.AiInteractionDto;
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
     * Returns the current tenant scope list for this module.
     */
    List<AiInteractionDto> list();
}
