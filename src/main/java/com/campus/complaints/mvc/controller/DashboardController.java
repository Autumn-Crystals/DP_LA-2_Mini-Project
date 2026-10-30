package com.campus.complaints.mvc.controller;

import com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.User;
import com.campus.complaints.mvc.view.AdminDashboardView;
import com.campus.complaints.mvc.view.StudentDashboardView;
import com.campus.complaints.service.AuthenticationService;
import com.campus.complaints.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * MVC Controller for dashboard operations.
 * Coordinates metrics computation via {@link DashboardService} and renders
 * updated statistics to {@link StudentDashboardView} and {@link AdminDashboardView}.
 */
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;
    private final AuthenticationService authService;
    private final ComplaintMediatorImpl mediator;
    private final StudentDashboardView studentView;
    private final AdminDashboardView adminView;

    public DashboardController(DashboardService dashboardService,
                               AuthenticationService authService,
                               ComplaintMediatorImpl mediator,
                               StudentDashboardView studentView,
                               AdminDashboardView adminView) {
        this.dashboardService = dashboardService;
        this.authService      = authService;
        this.mediator         = mediator;
        this.studentView      = studentView;
        this.adminView        = adminView;
    }

    /**
     * Refreshes the appropriate dashboard view based on current authenticated user.
     */
    public void refreshDashboard() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        if (user.isAdmin() && adminView != null) {
            Map<ComplaintStatus, Integer> counts = dashboardService.getAdminStats();
            adminView.updateStats(counts);
            log.info("Admin dashboard stats refreshed: {}", counts);
        } else if (studentView != null) {
            Map<ComplaintStatus, Integer> counts = dashboardService.getStudentStats(user.getId());
            studentView.updateStats(counts);
            log.info("Student dashboard stats refreshed for user #{}: {}", user.getId(), counts);
        }
    }
}
