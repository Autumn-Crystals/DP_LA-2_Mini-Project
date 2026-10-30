package com.campus.complaints.security;

import com.campus.complaints.model.User;
import com.campus.complaints.model.UserRole;

/**
 * Service enforcing role-based access control (RBAC) across views and actions.
 */
public class AccessControlService {

    public boolean canManageStaff(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public boolean canAssignStaff(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public boolean canResolveComplaint(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public boolean canUndoAction(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public boolean canSubmitComplaint(User user) {
        return user != null; // Both students and admins can file complaints
    }

    public boolean canViewAllComplaints(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}
