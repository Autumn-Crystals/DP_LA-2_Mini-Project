package com.campus.complaints.model;

/**
 * User roles in the system.
 */
public enum UserRole {
    STUDENT("Student"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
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
