package com.dala.crm.controller;

import com.dala.crm.dto.ActivityCreateRequest;
import com.dala.crm.dto.ActivityResponse;
import com.dala.crm.service.ActivityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for CRM activity management.
 */
@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACTIVITIES_WRITE)")
    public ActivityResponse createActivity(@Valid @RequestBody ActivityCreateRequest request) {
        return activityService.createActivity(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACTIVITIES_READ)")
    public List<ActivityResponse> getActivities() {
        return activityService.getActivities();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.dala.crm.security.CrmAuthorities).ACTIVITIES_READ)")
    public ActivityResponse getActivity(@PathVariable Long id) {
        return activityService.getActivity(id);
    }
}
