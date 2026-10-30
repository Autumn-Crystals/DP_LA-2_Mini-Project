package com.campus.complaints.designpatterns.mediator;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.mvc.view.*;
import com.campus.complaints.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>GoF Mediator Pattern</b> — Concrete Mediator Implementation.
 *
 * <p>Coordinates interactions between UI components and the business service layer
 * without allowing views to depend directly on one another.
 */
public class ComplaintMediatorImpl implements ComplaintMediator {

    private static final Logger log = LoggerFactory.getLogger(ComplaintMediatorImpl.class);

    // Coordinated components
    private MainFrame mainFrame;
    private ComplaintManagementView complaintManagementView;
    private AdminDashboardView adminDashboardView;
    private StudentDashboardView studentDashboardView;
    private ComplaintDetailsView detailsView;
    private ComplaintService complaintService;
    private int currentUserId;

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    public void setComplaintManagementView(ComplaintManagementView view) {
        this.complaintManagementView = view;
    }

    public void setAdminDashboardView(AdminDashboardView view) {
        this.adminDashboardView = view;
    }

    public void setStudentDashboardView(StudentDashboardView view) {
        this.studentDashboardView = view;
    }

    public void setDetailsView(ComplaintDetailsView view) {
        this.detailsView = view;
    }

    public void setComplaintService(ComplaintService service) {
        this.complaintService = service;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @Override
    public void onComplaintSubmitted(Complaint complaint) {
        log.info("Mediator: Complaint #{} submitted. Refreshing views and dispatching notification.", complaint.getId());
        refreshComplaintList();
        refreshDashboard();
        showNotification("Complaint submitted successfully: " + complaint.getTitle(), NotificationType.SUCCESS);
    }

    @Override
    public void onComplaintStatusChanged(Complaint complaint) {
        log.info("Mediator: Complaint #{} status changed to {}", complaint.getId(), complaint.getStatus());
        refreshComplaintList();
        refreshDashboard();
        if (detailsView != null) {
            refreshDetailsView(complaint);
        }
        showNotification("Status updated to: " + complaint.getStatus().getDisplayName(), NotificationType.SUCCESS);
    }

    @Override
    public void onStaffAssigned(Complaint complaint, MaintenanceStaff staff) {
        log.info("Mediator: Staff #{} assigned to complaint #{}", staff.getId(), complaint.getId());
        refreshComplaintList();
        refreshDashboard();
        if (detailsView != null) {
            refreshDetailsView(complaint);
        }
        showNotification("Complaint assigned to: " + staff.getName(), NotificationType.SUCCESS);
    }

    @Override
    public void onComplaintSelected(Complaint complaint) {
        log.debug("Mediator: Complaint #{} selected.", complaint.getId());
    }

    @Override
    public void onUndoPerformed(Complaint complaint) {
        log.info("Mediator: Undo performed on complaint #{}.", complaint.getId());
        refreshComplaintList();
        refreshDashboard();
        if (detailsView != null) {
            refreshDetailsView(complaint);
        }
        showNotification("Undo performed — previous state restored successfully.", NotificationType.INFO);
    }

    @Override
    public void showNotification(String message, NotificationType type) {
        if (mainFrame != null) {
            mainFrame.showNotification(message, type);
        }
        log.info("Notification [{}]: {}", type, message);
    }

    @Override
    public void refreshDashboard() {
        if (complaintService == null) return;
        try {
            if (adminDashboardView != null) {
                adminDashboardView.updateStats(complaintService.getDashboardStatsForAdmin());
            }
            if (studentDashboardView != null && currentUserId > 0) {
                studentDashboardView.updateStats(complaintService.getDashboardStatsForStudent(currentUserId));
            }
        } catch (Exception e) {
            log.warn("Dashboard refresh failed: {}", e.getMessage());
        }
    }

    @Override
    public void refreshComplaintList() {
        if (complaintManagementView != null && complaintService != null) {
            try {
                if (currentUserId > 0) {
                    complaintManagementView.loadComplaints(complaintService.getComplaintsByReporter(currentUserId));
                } else {
                    complaintManagementView.loadComplaints(complaintService.getAllComplaints());
                }
            } catch (Exception e) {
                log.warn("Failed to refresh complaint list: {}", e.getMessage());
            }
        }
    }

    private void refreshDetailsView(Complaint complaint) {
        if (detailsView == null || complaintService == null) return;
        try {
            complaintService.findById(complaint.getId()).ifPresent(fresh -> {
                complaint.setStatus(fresh.getStatus());
                complaint.setAssignedStaffId(fresh.getAssignedStaffId());
                complaint.setDescription(fresh.getDescription());
                complaint.setResolutionNotes(fresh.getResolutionNotes());
                complaint.setResolvedAt(fresh.getResolvedAt());
            });
        } catch (Exception e) {
            log.warn("Detail refresh failed: {}", e.getMessage());
        }
    }
}
