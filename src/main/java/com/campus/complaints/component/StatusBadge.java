package com.campus.complaints.component;

import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;

import java.awt.Color;

/**
 * Status and Priority badge component styled as a rounded pill.
 * Extends {@link BadgeLabel} for clean architecture and direct backward compatibility.
 */
public class StatusBadge extends BadgeLabel {

    public StatusBadge(String text, Color bg, Color fg) {
        super(text, bg, fg);
    }

    public static StatusBadge ofStatus(ComplaintStatus status) {
        BadgeLabel base = BadgeLabel.forStatus(status);
        return new StatusBadge(status.getDisplayName(), getStatusBg(status), getStatusFg(status));
    }

    public static StatusBadge ofPriority(Priority priority) {
        return new StatusBadge(priority.getDisplayName(), getPriorityBg(priority), getPriorityFg(priority));
    }

    private static Color getStatusBg(ComplaintStatus status) {
        return switch (status) {
            case PENDING     -> com.campus.complaints.util.AppTheme.STATUS_PENDING_BG;
            case ASSIGNED    -> com.campus.complaints.util.AppTheme.STATUS_ASSIGNED_BG;
            case IN_PROGRESS -> com.campus.complaints.util.AppTheme.STATUS_INPROGRESS_BG;
            case RESOLVED    -> com.campus.complaints.util.AppTheme.STATUS_RESOLVED_BG;
            case CANCELLED   -> com.campus.complaints.util.AppTheme.STATUS_CANCELLED_BG;
        };
    }

    private static Color getStatusFg(ComplaintStatus status) {
        return switch (status) {
            case PENDING     -> com.campus.complaints.util.AppTheme.STATUS_PENDING_FG;
            case ASSIGNED    -> com.campus.complaints.util.AppTheme.STATUS_ASSIGNED_FG;
            case IN_PROGRESS -> com.campus.complaints.util.AppTheme.STATUS_INPROGRESS_FG;
            case RESOLVED    -> com.campus.complaints.util.AppTheme.STATUS_RESOLVED_FG;
            case CANCELLED   -> com.campus.complaints.util.AppTheme.STATUS_CANCELLED_FG;
        };
    }

    private static Color getPriorityBg(Priority priority) {
        return switch (priority) {
            case LOW      -> com.campus.complaints.util.AppTheme.PRIORITY_LOW_BG;
            case MEDIUM   -> com.campus.complaints.util.AppTheme.PRIORITY_MED_BG;
            case HIGH     -> com.campus.complaints.util.AppTheme.PRIORITY_HIGH_BG;
            case CRITICAL -> com.campus.complaints.util.AppTheme.PRIORITY_CRIT_BG;
        };
    }

    private static Color getPriorityFg(Priority priority) {
        return switch (priority) {
            case LOW      -> com.campus.complaints.util.AppTheme.PRIORITY_LOW_FG;
            case MEDIUM   -> com.campus.complaints.util.AppTheme.PRIORITY_MED_FG;
            case HIGH     -> com.campus.complaints.util.AppTheme.PRIORITY_HIGH_FG;
            case CRITICAL -> com.campus.complaints.util.AppTheme.PRIORITY_CRIT_FG;
        };
    }
}
