package com.dala.crm.service;

import com.dala.crm.dto.ActivityCreateRequest;
import com.dala.crm.dto.ActivityResponse;
import java.util.List;

/**
 * Public use-case contract for task, note, and meeting activity management.
 */
public interface ActivityService {

    /**
     * Creates a new activity for the current tenant.
     */
    ActivityResponse createActivity(ActivityCreateRequest request);

    /**
     * Returns all activities for the current tenant ordered newest first.
     */
    List<ActivityResponse> getActivities();

    /**
     * Returns one activity by ID in current tenant scope.
     */
    ActivityResponse getActivity(Long id);
}
