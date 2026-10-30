package com.campus.complaints.model;

/**
 * Valid complaint lifecycle statuses.
 * Allowed transitions:
 *   PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED
 *   Any state -> CANCELLED (admin only)
 */
public enum ComplaintStatus {
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CANCELLED("Cancelled");

    private final String displayName;

    ComplaintStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns true if transitioning from this status to {@code next} is valid.
     */
    public boolean canTransitionTo(ComplaintStatus next) {
        return switch (this) {
            case PENDING    -> next == ASSIGNED   || next == CANCELLED;
            case ASSIGNED   -> next == IN_PROGRESS || next == CANCELLED || next == PENDING;
            case IN_PROGRESS -> next == RESOLVED  || next == CANCELLED;
            case RESOLVED, CANCELLED -> false;
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
