package com.dala.crm.impl;

import com.dala.crm.dto.CampaignCreateRequest;
import com.dala.crm.dto.CampaignResponse;
import com.dala.crm.entity.Activity;
import com.dala.crm.entity.AudienceSegment;
import com.dala.crm.entity.Campaign;
import com.dala.crm.exception.BadRequestException;
import com.dala.crm.exception.CampaignNotFoundException;
import com.dala.crm.repo.ActivityRepository;
import com.dala.crm.repo.AudienceSegmentRepository;
import com.dala.crm.repo.CampaignRepository;
import com.dala.crm.security.TenantContext;
import com.dala.crm.service.AuditLogService;
import com.dala.crm.service.CampaignService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default campaign service implementation.
 */
@Service
@Transactional
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final AudienceSegmentRepository audienceSegmentRepository;
    private final ActivityRepository activityRepository;
    private final AuditLogService auditLogService;

    public CampaignServiceImpl(
            CampaignRepository campaignRepository,
            AudienceSegmentRepository audienceSegmentRepository,
            ActivityRepository activityRepository,
            AuditLogService auditLogService
    ) {
        this.campaignRepository = campaignRepository;
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.activityRepository = activityRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public CampaignResponse create(CampaignCreateRequest request) {
        String tenantId = currentTenant();
        AudienceSegment segment = currentSegment(tenantId, request.audienceSegmentId());
        Instant now = Instant.now();

        Campaign campaign = new Campaign();
        campaign.setTenantId(tenantId);
        campaign.setName(request.name().trim());
        campaign.setChannelType(normalize(request.channelType()));
        campaign.setStatus(normalize(request.status()));
        campaign.setAudienceSegmentId(segment.getId());
        campaign.setSubject(request.subject().trim());
        campaign.setBody(request.body().trim());
        campaign.setScheduledAt(request.scheduledAt());
        campaign.setCreatedAt(now);

        Campaign savedCampaign = campaignRepository.save(campaign);
        auditLogService.record(
                "CREATE",
                "CAMPAIGN",
                savedCampaign.getId(),
                "Created campaign " + savedCampaign.getName() + " for segment " + segment.getName()
        );
        recordTimelineActivity(savedCampaign, segment.getName(), now);
        return toResponse(savedCampaign, segment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignResponse> list() {
        String tenantId = currentTenant();
        return campaignRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(campaign -> toResponse(campaign, currentSegment(tenantId, campaign.getAudienceSegmentId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignResponse get(Long id) {
        Campaign campaign = currentCampaign(id);
        AudienceSegment segment = currentSegment(campaign.getTenantId(), campaign.getAudienceSegmentId());
        return toResponse(campaign, segment);
    }

    private void recordTimelineActivity(Campaign campaign, String segmentName, Instant createdAt) {
        Activity activity = new Activity();
        activity.setTenantId(campaign.getTenantId());
        activity.setType("CAMPAIGN");
        activity.setSubject("Campaign created: " + campaign.getName());
        activity.setRelatedEntityType("CAMPAIGN");
        activity.setRelatedEntityId(campaign.getId());
        activity.setDetails("Audience segment: " + segmentName + ", channel: " + campaign.getChannelType());
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private Campaign currentCampaign(Long id) {
        String tenantId = currentTenant();
        return campaignRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new CampaignNotFoundException(id));
    }

    private AudienceSegment currentSegment(String tenantId, Long id) {
        return audienceSegmentRepository.findById(id)
                .filter(record -> record.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadRequestException("Audience segment not found with id " + id));
    }

    private String currentTenant() {
        return TenantContext.getTenantId()
                .orElseThrow(() -> new BadRequestException("Missing required header: X-Tenant-Id"));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private CampaignResponse toResponse(Campaign campaign, AudienceSegment segment) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getChannelType(),
                campaign.getStatus(),
                campaign.getAudienceSegmentId(),
                segment.getName(),
                campaign.getSubject(),
                campaign.getBody(),
                campaign.getScheduledAt(),
                campaign.getCreatedAt()
        );
    }
}
