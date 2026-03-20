package com.dala.crm.impl;

import com.dala.crm.dto.TenantProfileDto;
import com.dala.crm.repo.TenantProfileRepository;
import com.dala.crm.service.TenantProfileService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default identitytenancy service implementation.
 */
@Service
@Transactional(readOnly = true)
public class TenantProfileServiceImpl implements TenantProfileService {

    private final TenantProfileRepository repository;

    public TenantProfileServiceImpl(TenantProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TenantProfileDto> list() {
        return repository.findAll().stream()
                .map(record -> new TenantProfileDto(record.getId(), record.getName()))
                .toList();
    }
}
