package com.dala.crm.impl;

import com.dala.crm.dto.AiInteractionDto;
import com.dala.crm.repo.AiInteractionRepository;
import com.dala.crm.service.AiInteractionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default aiassistant service implementation.
 */
@Service
@Transactional(readOnly = true)
public class AiInteractionServiceImpl implements AiInteractionService {

    private final AiInteractionRepository repository;

    public AiInteractionServiceImpl(AiInteractionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AiInteractionDto> list() {
        return repository.findAll().stream()
                .map(record -> new AiInteractionDto(record.getId(), record.getName()))
                .toList();
    }
}
