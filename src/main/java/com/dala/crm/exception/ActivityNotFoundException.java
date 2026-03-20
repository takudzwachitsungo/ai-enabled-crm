package com.dala.crm.exception;

/**
 * Raised when an activity ID is not present within the current tenant scope.
 */
public class ActivityNotFoundException extends NotFoundException {

    public ActivityNotFoundException(Long activityId) {
        super("Activity not found: " + activityId);
    }
}
