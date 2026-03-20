package com.dala.crm.impl;

import com.dala.crm.dto.WorkflowDefinitionDto;
import com.dala.crm.repo.WorkflowDefinitionRepository;
import com.dala.crm.service.WorkflowDefinitionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default workflow service implementation.
 */
@Service
@Transactional(readOnly = true)
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    private final WorkflowDefinitionRepository repository;

    public WorkflowDefinitionServiceImpl(WorkflowDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WorkflowDefinitionDto> list() {
        return repository.findAll().stream()
                .map(record -> new WorkflowDefinitionDto(record.getId(), record.getName()))
                .toList();
    }
}
