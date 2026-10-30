package com.campus.complaints.model;

/**
 * Complaint categories mapped to maintenance staff skills.
 */
public enum ComplaintCategory {
    NETWORK("Network / Wi-Fi"),
    ELECTRICAL("Electrical"),
    PLUMBING("Plumbing"),
    EQUIPMENT("Equipment"),
    CLEANING("Cleaning / Sanitation"),
    IT_SUPPORT("IT Support"),
    GENERAL("General Maintenance");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
