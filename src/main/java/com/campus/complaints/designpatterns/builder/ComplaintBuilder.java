package com.campus.complaints.designpatterns.builder;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;

import java.time.LocalDateTime;

/**
 * <b>GoF Builder Pattern</b> — Concrete Builder.
 *
 * <p>Constructs complex {@link Complaint} objects step-by-step.
 * Enforces validation of mandatory fields before instantiation and provides
 * a fluent API for configuring optional lifecycle attributes.
 */
public class ComplaintBuilder {

    // Mandatory fields
    private String title;
    private String description;
    private ComplaintCategory category;
    private String location;
    private Priority priority;
    private int reporterId;

    // Optional fields with defaults
    private int id = 0;
    private ComplaintStatus status = ComplaintStatus.PENDING;
    private Integer assignedStaffId = null;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime resolvedAt = null;
    private String resolutionNotes = null;

    public ComplaintBuilder() {}

    public ComplaintBuilder id(int id) {
        this.id = id;
        return this;
    }

    public ComplaintBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ComplaintBuilder description(String desc) {
        this.description = desc;
        return this;
    }

    public ComplaintBuilder category(ComplaintCategory cat) {
        this.category = cat;
        return this;
    }

    public ComplaintBuilder location(String loc) {
        this.location = loc;
        return this;
    }

    public ComplaintBuilder priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public ComplaintBuilder reporterId(int rid) {
        this.reporterId = rid;
        return this;
    }

    public ComplaintBuilder status(ComplaintStatus status) {
        this.status = status;
        return this;
    }

    public ComplaintBuilder assignedStaffId(Integer sid) {
        this.assignedStaffId = sid;
        return this;
    }

    public ComplaintBuilder createdAt(LocalDateTime dt) {
        this.createdAt = dt;
        return this;
    }

    public ComplaintBuilder updatedAt(LocalDateTime dt) {
        this.updatedAt = dt;
        return this;
    }

    public ComplaintBuilder resolvedAt(LocalDateTime dt) {
        this.resolvedAt = dt;
        return this;
    }

    public ComplaintBuilder resolutionNotes(String notes) {
        this.resolutionNotes = notes;
        return this;
    }

    /**
     * Validates required constraints and produces a valid {@link Complaint}.
     *
     * @throws IllegalStateException if any mandatory field is missing or invalid
     */
    public Complaint build() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalStateException("Title is required and cannot be blank.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalStateException("Description is required and cannot be blank.");
        }
        if (category == null) {
            throw new IllegalStateException("Category is required.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalStateException("Location is required and cannot be blank.");
        }
        if (priority == null) {
            throw new IllegalStateException("Priority is required.");
        }
        if (reporterId <= 0) {
            throw new IllegalStateException("Valid reporter ID is required.");
        }

        Complaint complaint = new Complaint();
        complaint.setId(this.id);
        complaint.setTitle(this.title.trim());
        complaint.setDescription(this.description.trim());
        complaint.setCategory(this.category);
        complaint.setLocation(this.location.trim());
        complaint.setPriority(this.priority);
        complaint.setStatus(this.status != null ? this.status : ComplaintStatus.PENDING);
        complaint.setReporterId(this.reporterId);
        complaint.setAssignedStaffId(this.assignedStaffId);
        complaint.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        complaint.setUpdatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now());
        complaint.setResolvedAt(this.resolvedAt);
        complaint.setResolutionNotes(this.resolutionNotes);

        return complaint;
    }
}
