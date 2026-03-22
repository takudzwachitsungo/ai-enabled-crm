package com.dala.crm.service;

import com.dala.crm.dto.CustomEntityDefinitionCreateRequest;
import com.dala.crm.dto.CustomEntityDefinitionDto;
import com.dala.crm.dto.CustomEntityRecordCreateRequest;
import com.dala.crm.dto.CustomEntityRecordDto;
import java.util.List;

/**
 * Public contract for platform custom entity management.
 */
public interface CustomEntityService {

    CustomEntityDefinitionDto createDefinition(CustomEntityDefinitionCreateRequest request);

    List<CustomEntityDefinitionDto> listDefinitions();

    CustomEntityDefinitionDto getDefinition(Long id);

    CustomEntityRecordDto createRecord(Long definitionId, CustomEntityRecordCreateRequest request);

    List<CustomEntityRecordDto> listRecords(Long definitionId);

    CustomEntityRecordDto getRecord(Long definitionId, Long recordId);
}
