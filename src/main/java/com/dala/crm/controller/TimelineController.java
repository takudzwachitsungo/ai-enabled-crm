package com.dala.crm.controller;

import com.dala.crm.dto.ActivityResponse;
import com.dala.crm.service.ActivityService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only timeline feed for tenant-scoped CRM activity.
 */
@RestController
@RequestMapping("/api/v1/timeline")
public class TimelineController {

    private final ActivityService activityService;

    public TimelineController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACTIVITIES_READ)")
    public List<ActivityResponse> getTimeline() {
        return activityService.getActivities();
    }
}
