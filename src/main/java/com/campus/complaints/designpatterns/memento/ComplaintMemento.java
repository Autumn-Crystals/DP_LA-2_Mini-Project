package com.campus.complaints.designpatterns.memento;

import com.campus.complaints.model.ComplaintStatus;

import java.time.LocalDateTime;

/**
 * <b>GoF Memento Pattern</b> — Memento (State Snapshot).
 *
 * <p>Captures an immutable snapshot of a complaint's mutable state:
 * status, assignedStaffId, description, and resolutionNotes.
 * Prevents outside modification to ensure encapsulation.
 */
public final class ComplaintMemento {

    private final int complaintId;
    private final ComplaintStatus status;
    private final Integer assignedStaffId;
    private final String description;
    private final String resolutionNotes;
    private final LocalDateTime timestamp;
    private final int changedByUserId;
    private final String actionLabel;

    public ComplaintMemento(int complaintId,
                            ComplaintStatus status,
                            Integer assignedStaffId,
                            String description,
                            String resolutionNotes,
                            int changedByUserId,
                            String actionLabel) {
        this.complaintId     = complaintId;
        this.status          = status;
        this.assignedStaffId = assignedStaffId;
        this.description     = description;
        this.resolutionNotes = resolutionNotes;
        this.timestamp       = LocalDateTime.now();
        this.changedByUserId = changedByUserId;
        this.actionLabel     = actionLabel;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public Integer getAssignedStaffId() {
        return assignedStaffId;
    }

    public String getDescription() {
        return description;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getChangedByUserId() {
        return changedByUserId;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    @Override
    public String toString() {
        return actionLabel + " @ " + timestamp;
    }
}
