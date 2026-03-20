package com.dala.crm.impl;

import com.dala.crm.dto.ConversationRecordDto;
import com.dala.crm.repo.ConversationRecordRepository;
import com.dala.crm.service.ConversationRecordService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default communication service implementation.
 */
@Service
@Transactional(readOnly = true)
public class ConversationRecordServiceImpl implements ConversationRecordService {

    private final ConversationRecordRepository repository;

    public ConversationRecordServiceImpl(ConversationRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ConversationRecordDto> list() {
        return repository.findAll().stream()
                .map(record -> new ConversationRecordDto(record.getId(), record.getName()))
                .toList();
    }
}
