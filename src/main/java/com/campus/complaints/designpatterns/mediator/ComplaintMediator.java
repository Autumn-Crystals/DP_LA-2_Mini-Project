package com.campus.complaints.designpatterns.mediator;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;

/**
 * <b>GoF Mediator Pattern</b> — Mediator Interface.
 *
 * <p>Decouples presentation views (ComplaintFormView, ComplaintManagementView,
 * Dashboard views, ComplaintDetailsView, and NotificationBanner) from each other.
 * Cross-component interactions are routed solely through this interface.
 */
public interface ComplaintMediator {

    void onComplaintSubmitted(Complaint complaint);

    void onComplaintStatusChanged(Complaint complaint);

    void onStaffAssigned(Complaint complaint, MaintenanceStaff staff);

    void onComplaintSelected(Complaint complaint);

    void onUndoPerformed(Complaint complaint);

    void showNotification(String message, NotificationType type);

    void refreshDashboard();

    void refreshComplaintList();

    enum NotificationType {
        SUCCESS, INFO, WARNING, ERROR
    }
}
