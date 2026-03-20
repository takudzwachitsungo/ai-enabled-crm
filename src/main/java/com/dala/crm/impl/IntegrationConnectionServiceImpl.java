package com.dala.crm.impl;

import com.dala.crm.dto.IntegrationConnectionDto;
import com.dala.crm.repo.IntegrationConnectionRepository;
import com.dala.crm.service.IntegrationConnectionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default integration service implementation.
 */
@Service
@Transactional(readOnly = true)
public class IntegrationConnectionServiceImpl implements IntegrationConnectionService {

    private final IntegrationConnectionRepository repository;

    public IntegrationConnectionServiceImpl(IntegrationConnectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<IntegrationConnectionDto> list() {
        return repository.findAll().stream()
                .map(record -> new IntegrationConnectionDto(record.getId(), record.getName()))
                .toList();
    }
}
