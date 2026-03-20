package com.dala.crm.impl;

import com.dala.crm.dto.ReportSnapshotDto;
import com.dala.crm.repo.ReportSnapshotRepository;
import com.dala.crm.service.ReportSnapshotService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default reporting service implementation.
 */
@Service
@Transactional(readOnly = true)
public class ReportSnapshotServiceImpl implements ReportSnapshotService {

    private final ReportSnapshotRepository repository;

    public ReportSnapshotServiceImpl(ReportSnapshotRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ReportSnapshotDto> list() {
        return repository.findAll().stream()
                .map(record -> new ReportSnapshotDto(record.getId(), record.getName()))
                .toList();
    }
}
