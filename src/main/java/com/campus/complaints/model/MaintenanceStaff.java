package com.campus.complaints.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a maintenance staff member.
 * Includes skills for SkillBasedAssignmentStrategy.
 */
public class MaintenanceStaff {

    public enum AvailabilityStatus {
        AVAILABLE("Available"),
        UNAVAILABLE("Unavailable"),
        ON_LEAVE("On Leave");

        private final String displayName;
        AvailabilityStatus(String d) { this.displayName = d; }
        public String getDisplayName() { return displayName; }
        @Override public String toString() { return displayName; }
    }

    private int id;
    private int userId;
    private String name;
    private String email;
    private String phone;
    private AvailabilityStatus availabilityStatus;
    private List<ComplaintCategory> skills;
    private int activeComplaintCount; // transient — populated by queries
    private LocalDateTime createdAt;

    public MaintenanceStaff() {
        this.skills = new ArrayList<>();
    }

    public MaintenanceStaff(int id, int userId, String name, String email,
                             String phone, AvailabilityStatus status,
                             LocalDateTime createdAt) {
        this.id                 = id;
        this.userId             = userId;
        this.name               = name;
        this.email              = email;
        this.phone              = phone;
        this.availabilityStatus = status;
        this.createdAt          = createdAt;
        this.skills             = new ArrayList<>();
    }

    // ---- Getters & Setters ----

    public int getId()                                        { return id; }
    public void setId(int id)                               { this.id = id; }

    public int getUserId()                                    { return userId; }
    public void setUserId(int userId)                       { this.userId = userId; }

    public String getName()                                   { return name; }
    public void setName(String name)                        { this.name = name; }

    public String getEmail()                                  { return email; }
    public void setEmail(String email)                      { this.email = email; }

    public String getPhone()                                  { return phone; }
    public void setPhone(String phone)                      { this.phone = phone; }

    public AvailabilityStatus getAvailabilityStatus()         { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus s) { this.availabilityStatus = s; }

    public List<ComplaintCategory> getSkills()                { return skills; }
    public void setSkills(List<ComplaintCategory> skills)   { this.skills = skills; }
    public void addSkill(ComplaintCategory cat)             { this.skills.add(cat); }

    public int getActiveComplaintCount()                      { return activeComplaintCount; }
    public void setActiveComplaintCount(int count)          { this.activeComplaintCount = count; }

    public LocalDateTime getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)              { this.createdAt = dt; }

    public boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    public boolean hasSkillFor(ComplaintCategory category) {
        return skills.contains(category);
    }

    @Override
    public String toString() {
        return name + " (" + (availabilityStatus != null ? availabilityStatus.getDisplayName() : "Unknown") + ")";
    }
}
