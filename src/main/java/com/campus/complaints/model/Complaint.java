package com.campus.complaints.model;

import java.time.LocalDateTime;

/**
 * Core domain model for a campus complaint.
 *
 * <p><b>Builder Pattern</b>: The static inner {@code Builder} class is the
 * canonical way to construct Complaint instances. This ensures all required
 * fields are validated before the object is created, and allows optional
 * fields to be set fluently.
 *
 * <p><b>Prototype Pattern</b>: {@link #deepCopy()} produces an independent
 * duplicate suitable for "Duplicate Complaint" functionality.
 */
import com.campus.complaints.designpatterns.prototype.ComplaintPrototype;

public class Complaint implements ComplaintPrototype {

    // ---- Fields ----
    private int id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private String location;
    private Priority priority;
    private ComplaintStatus status;
    private int reporterId;
    private Integer assignedStaffId;        // nullable
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;       // nullable
    private String resolutionNotes;         // nullable

    // Public constructor
    public Complaint() {}

    // ---- Getters ----
    public int getId()                          { return id; }
    public String getTitle()                    { return title; }
    public String getDescription()              { return description; }
    public ComplaintCategory getCategory()      { return category; }
    public String getLocation()                 { return location; }
    public Priority getPriority()               { return priority; }
    public ComplaintStatus getStatus()          { return status; }
    public int getReporterId()                  { return reporterId; }
    public Integer getAssignedStaffId()         { return assignedStaffId; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public LocalDateTime getResolvedAt()        { return resolvedAt; }
    public String getResolutionNotes()          { return resolutionNotes; }

    // ---- Setters (used by DAO and undo operations) ----
    public void setId(int id)                           { this.id = id; }
    public void setTitle(String t)                      { this.title = t; }
    public void setDescription(String d)                { this.description = d; }
    public void setCategory(ComplaintCategory c)        { this.category = c; }
    public void setLocation(String l)                   { this.location = l; }
    public void setPriority(Priority p)                 { this.priority = p; }
    public void setStatus(ComplaintStatus s)            { this.status = s; }
    public void setReporterId(int rid)                  { this.reporterId = rid; }
    public void setAssignedStaffId(Integer sid)         { this.assignedStaffId = sid; }
    public void setCreatedAt(LocalDateTime dt)          { this.createdAt = dt; }
    public void setUpdatedAt(LocalDateTime dt)          { this.updatedAt = dt; }
    public void setResolvedAt(LocalDateTime dt)         { this.resolvedAt = dt; }
    public void setResolutionNotes(String n)            { this.resolutionNotes = n; }

    // ---- Prototype Pattern ----

    /**
     * Creates a deep copy of this complaint for the Duplicate Complaint feature.
     * The copy has:
     * <ul>
     *   <li>id = 0 (unsaved)</li>
     *   <li>status = PENDING</li>
     *   <li>assignedStaffId = null</li>
     *   <li>createdAt = updatedAt = now</li>
     *   <li>resolvedAt = null, resolutionNotes = null</li>
     * </ul>
     */
    public Complaint deepCopy() {
        Complaint copy = new Complaint();
        copy.id               = 0;                        // unsaved — new identity
        copy.title            = this.title + " (Copy)";
        copy.description      = this.description;
        copy.category         = this.category;
        copy.location         = this.location;
        copy.priority         = this.priority;
        copy.status           = ComplaintStatus.PENDING;   // always reset
        copy.reporterId       = this.reporterId;
        copy.assignedStaffId  = null;                     // not yet assigned
        LocalDateTime now     = LocalDateTime.now();
        copy.createdAt        = now;
        copy.updatedAt        = now;
        copy.resolvedAt       = null;
        copy.resolutionNotes  = null;
        return copy;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " (" + (status != null ? status.getDisplayName() : "?") + ")";
    }

    // ====================================================================
    // Builder Pattern — static inner Builder class
    // ====================================================================

    /**
     * Fluent builder for {@link Complaint}.
     *
     * <p>Required fields: title, description, category, location, priority,
     * reporterId.
     *
     * <p>Optional fields: id, assignedStaffId, status, createdAt, updatedAt,
     * resolvedAt, resolutionNotes.
     */
    public static class Builder extends com.campus.complaints.designpatterns.builder.ComplaintBuilder {
        public Builder() {
            super();
        }
    }
}
