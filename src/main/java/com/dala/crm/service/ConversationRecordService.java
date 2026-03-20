package com.dala.crm.service;

import com.dala.crm.dto.ConversationRecordDto;
import java.util.List;

/**
 * Public service contract for communication module use cases.
 */
public interface ConversationRecordService {

    /**
     * Returns the current tenant scope list for this module.
     */
    List<ConversationRecordDto> list();
}
