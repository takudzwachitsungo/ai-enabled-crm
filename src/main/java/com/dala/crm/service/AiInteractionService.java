package com.dala.crm.service;

import com.dala.crm.dto.AiInteractionDto;
import java.util.List;

/**
 * Public service contract for aiassistant module use cases.
 */
public interface AiInteractionService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<AiInteractionDto> list();
}
