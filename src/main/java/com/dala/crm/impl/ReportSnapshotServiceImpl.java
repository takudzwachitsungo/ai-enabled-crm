package com.dala.crm.impl;

import com.dala.crm.dto.ReportSnapshotCreateRequest;
import com.dala.crm.dto.ReportSnapshotDto;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.ReportSnapshot;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.ReportSnapshotException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.AudienceSegmentRepository;
import com.dala.crm.repo.CampaignRepository;
import com.dala.crm.repo.CannedResponseRepository;
import com.dala.crm.repo.KnowledgeBaseArticleRepository;
import com.dala.crm.repo.ReportSnapshotRepository;
import com.dala.crm.repo.TicketRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.ReportSnapshotService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default reporting service implementation.
 */
@Service
@Transactional(readOnly = true)
public class ReportSnapshotServiceImpl implements ReportSnapshotService {

    private final ReportSnapshotRepository repository;
    private final KnowledgeBaseArticleRepository knowledgeBaseArticleRepository;
    private final CannedResponseRepository cannedResponseRepository;
    private final AudienceSegmentRepository audienceSegmentRepository;
    private final CampaignRepository campaignRepository;
    private final TicketRepository ticketRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public ReportSnapshotServiceImpl(
            ReportSnapshotRepository repository,
            KnowledgeBaseArticleRepository knowledgeBaseArticleRepository,
            CannedResponseRepository cannedResponseRepository,
            AudienceSegmentRepository audienceSegmentRepository,
            CampaignRepository campaignRepository,
            TicketRepository ticketRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.knowledgeBaseArticleRepository = knowledgeBaseArticleRepository;
        this.cannedResponseRepository = cannedResponseRepository;
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.campaignRepository = campaignRepository;
        this.ticketRepository = ticketRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public ReportSnapshotDto create(ReportSnapshotCreateRequest request) {
        String tenantId = currentTenant();
        Instant now = Instant.now();

        ReportSnapshot snapshot = new ReportSnapshot();
        snapshot.setTenantId(tenantId);
        snapshot.setName(request.name().trim());
        snapshot.setReportType(normalize(request.reportType()));
        snapshot.setDeliveryChannel(normalize(request.deliveryChannel()));
        snapshot.setScheduleCadence(normalize(request.scheduleCadence()));
        snapshot.setStatus("GENERATED");
        snapshot.setSnapshotPayload(buildSnapshotPayload(tenantId));
        snapshot.setGeneratedAt(now);
        snapshot.setCreatedAt(now);

        ReportSnapshot saved = repository.save(snapshot);
        auditLogService.record("CREATE", "REPORT_SNAPSHOT", saved.getId(), "Generated report snapshot " + saved.getName());
        recordTimelineActivity(saved, now);
        return toDto(saved);
    }

    @Override
    public List<ReportSnapshotDto> list() {
        return repository.findByTenantIdOrderByCreatedAtDesc(currentTenant()).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ReportSnapshotDto get(Long id) {
        return repository.findById(id)
                .filter(record -> record.getTenantId().equals(currentTenant()))
                .map(this::toDto)
                .orElseThrow(() -> new ReportSnapshotException("Report snapshot not found: " + id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String buildSnapshotPayload(String tenantId) {
        long publishedKnowledgeArticles = knowledgeBaseArticleRepository.countByTenantIdAndPublishedTrue(tenantId);
        long cannedResponses = cannedResponseRepository.countByTenantId(tenantId);
        long activeSegments = audienceSegmentRepository.countByTenantIdAndActiveTrue(tenantId);
        long draftCampaigns = campaignRepository.countByTenantIdAndStatus(tenantId, "DRAFT");
        long scheduledCampaigns = campaignRepository.countByTenantIdAndStatus(tenantId, "SCHEDULED");
        long openTickets = ticketRepository.countByTenantIdAndStatus(tenantId, "OPEN");
        long escalatedTickets = ticketRepository.countByTenantIdAndStatus(tenantId, "ESCALATED");
        return "publishedKnowledgeArticles=" + publishedKnowledgeArticles
                + "; cannedResponses=" + cannedResponses
                + "; activeAudienceSegments=" + activeSegments
                + "; draftCampaigns=" + draftCampaigns
                + "; scheduledCampaigns=" + scheduledCampaigns
                + "; openTickets=" + openTickets
                + "; escalatedTickets=" + escalatedTickets;
    }

    private void recordTimelineActivity(ReportSnapshot snapshot, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(snapshot.getTenantId());
        activity.setType("REPORT");
        activity.setSubject("Report generated: " + snapshot.getName());
        activity.setRelatedEntityType("REPORT_SNAPSHOT");
        activity.setRelatedEntityId(snapshot.getId());
        activity.setDetails("Delivery channel: " + snapshot.getDeliveryChannel() + ", cadence: " + snapshot.getScheduleCadence());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private ReportSnapshotDto toDto(ReportSnapshot record) {
        return new ReportSnapshotDto(
                record.getId(),
                record.getName(),
                record.getReportType(),
                record.getDeliveryChannel(),
                record.getScheduleCadence(),
                record.getStatus(),
                record.getSnapshotPayload(),
                record.getGeneratedAt(),
                record.getCreatedAt()
        );
    }
}
