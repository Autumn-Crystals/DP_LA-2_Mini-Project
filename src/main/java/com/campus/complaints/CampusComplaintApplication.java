package com.campus.complaints;

import com.campus.complaints.config.DatabaseConfiguration;
import com.campus.complaints.dao.inmemory.InMemoryDatabase;
import com.campus.complaints.designpatterns.mediator.ComplaintMediator;
import com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl;
import com.campus.complaints.model.User;
import com.campus.complaints.model.UserRole;
import com.campus.complaints.mvc.controller.ComplaintController;
import com.campus.complaints.mvc.controller.DashboardController;
import com.campus.complaints.mvc.controller.LoginController;
import com.campus.complaints.mvc.controller.StaffController;
import com.campus.complaints.mvc.view.AdminDashboardView;
import com.campus.complaints.mvc.view.ComplaintDetailsView;
import com.campus.complaints.mvc.view.ComplaintFormView;
import com.campus.complaints.mvc.view.ComplaintManagementView;
import com.campus.complaints.mvc.view.LoginView;
import com.campus.complaints.mvc.view.MainFrame;
import com.campus.complaints.mvc.view.StaffManagementView;
import com.campus.complaints.mvc.view.StudentDashboardView;
import com.campus.complaints.service.AuthenticationService;
import com.campus.complaints.service.ComplaintService;
import com.campus.complaints.service.DashboardService;
import com.campus.complaints.service.StaffAssignmentService;
import com.campus.complaints.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for the Smart Campus Complaint & Maintenance System.
 *
 * <p>Orchestrates the MVC architecture, service layer, and six GoF design patterns:
 * <ul>
 *   <li><b>Builder</b>: Construction and validation of complaint requests.</li>
 *   <li><b>Prototype</b>: Cloning existing complaints for quick re-submission.</li>
 *   <li><b>Mediator</b>: Decoupled coordination between UI views and notifications.</li>
 *   <li><b>Template Method</b>: Categorized workflows for resolving complaints.</li>
 *   <li><b>Strategy</b>: Dynamic runtime selection of staff assignment algorithms.</li>
 *   <li><b>Memento</b>: Multi-level undo mechanism restoring previous complaint states.</li>
 * </ul>
 */
public class CampusComplaintApplication {

    private static final Logger log = LoggerFactory.getLogger(CampusComplaintApplication.class);

    // ---- MVC Views ----
    private static MainFrame                mainFrame;
    private static LoginView                loginView;
    private static StudentDashboardView     studentDashboard;
    private static AdminDashboardView       adminDashboard;
    private static ComplaintManagementView  complaintManagementView;
    private static ComplaintDetailsView     complaintDetails;
    private static ComplaintFormView        complaintForm;
    private static StaffManagementView      staffManagement;

    // ---- Services ----
    private static AuthenticationService   authService;
    private static ComplaintService        complaintService;
    private static StaffAssignmentService  staffAssignmentService;
    private static DashboardService        dashboardService;
    private static StaffService            staffService;

    // ---- MVC Controllers ----
    private static LoginController         loginController;
    private static ComplaintController     complaintController;
    private static DashboardController     dashboardController;
    private static StaffController         staffController;

    // ---- GoF Mediator ----
    private static ComplaintMediatorImpl   mediator;

    // ---- Card Layout View Keys ----
    private static final String VIEW_LOGIN            = "login";
    private static final String VIEW_STUDENT_DASH     = "student_dash";
    private static final String VIEW_ADMIN_DASH       = "admin_dash";
    private static final String VIEW_COMPLAINTS       = "complaints";
    private static final String VIEW_COMPLAINT_FORM   = "complaint_form";
    private static final String VIEW_COMPLAINT_DETAIL = "complaint_detail";
    private static final String VIEW_STAFF            = "staff";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setGlobalFont(new Font("Segoe UI", Font.PLAIN, 13));
        SwingUtilities.invokeLater(CampusComplaintApplication::bootstrap);
    }

    private static void bootstrap() {
        String dbError = DatabaseConfiguration.testConnection();
        boolean isDemoMode = false;

        if (dbError != null) {
            log.warn("Database connection unavailable ({}). Launching in Offline Demo Mode with preloaded campus data.", dbError);
            isDemoMode = true;

            InMemoryDatabase.InMemoryUserDAO userDAO = new InMemoryDatabase.InMemoryUserDAO();
            InMemoryDatabase.InMemoryComplaintDAO complaintDAO = new InMemoryDatabase.InMemoryComplaintDAO();
            InMemoryDatabase.InMemoryStaffDAO staffDAO = new InMemoryDatabase.InMemoryStaffDAO();
            InMemoryDatabase.InMemoryComplaintHistoryDAO historyDAO = new InMemoryDatabase.InMemoryComplaintHistoryDAO();
            InMemoryDatabase.seed(userDAO, complaintDAO, staffDAO);

            authService            = new AuthenticationService(userDAO);
            complaintService       = new ComplaintService(complaintDAO, staffDAO, historyDAO);
            staffAssignmentService = new StaffAssignmentService(staffDAO);
            dashboardService       = new DashboardService(complaintDAO, staffDAO);
            staffService           = new StaffService(staffDAO, userDAO);
        } else {
            authService            = new AuthenticationService();
            complaintService       = new ComplaintService();
            staffAssignmentService = new StaffAssignmentService();
            dashboardService       = new DashboardService();
            staffService           = new StaffService();
        }

        // Initialize GoF Mediator
        mediator = new ComplaintMediatorImpl();
        mediator.setComplaintService(complaintService);

        // Instantiate Frame & Views
        mainFrame               = new MainFrame();
        loginView               = new LoginView();
        studentDashboard        = new StudentDashboardView();
        adminDashboard          = new AdminDashboardView();
        complaintManagementView = new ComplaintManagementView(false);
        complaintDetails        = new ComplaintDetailsView(true);
        complaintForm           = new ComplaintFormView();
        staffManagement         = new StaffManagementView();

        // Register views with Mediator
        mediator.setMainFrame(mainFrame);
        mediator.setStudentDashboardView(studentDashboard);
        mediator.setAdminDashboardView(adminDashboard);
        mediator.setComplaintManagementView(complaintManagementView);
        mediator.setDetailsView(complaintDetails);

        // Add views to main card deck
        mainFrame.addView(VIEW_LOGIN,            loginView);
        mainFrame.addView(VIEW_STUDENT_DASH,     studentDashboard);
        mainFrame.addView(VIEW_ADMIN_DASH,       adminDashboard);
        mainFrame.addView(VIEW_COMPLAINTS,       complaintManagementView);
        mainFrame.addView(VIEW_COMPLAINT_FORM,   complaintForm);
        mainFrame.addView(VIEW_COMPLAINT_DETAIL, complaintDetails);
        mainFrame.addView(VIEW_STAFF,            staffManagement);

        // Instantiate Controllers
        loginController = new LoginController(loginView, authService, mainFrame,
                CampusComplaintApplication::onAdminLogin, CampusComplaintApplication::onStudentLogin);

        complaintController = new ComplaintController(complaintService, staffService,
                authService, mediator, complaintManagementView, complaintDetails, complaintForm);
        complaintController.setShowListAction(()        -> showView(VIEW_COMPLAINTS));
        complaintController.setShowFormAction(()        -> showView(VIEW_COMPLAINT_FORM));
        complaintController.setShowDetailsViewAction(() -> showView(VIEW_COMPLAINT_DETAIL));

        dashboardController = new DashboardController(dashboardService, authService,
                mediator, studentDashboard, adminDashboard);

        staffController = new StaffController(staffService, staffManagement, mediator);

        // Attach Dashboard Listeners
        studentDashboard.addSubmitListener(e -> showView(VIEW_COMPLAINT_FORM));
        studentDashboard.addViewAllListener(e -> {
            complaintController.refreshList();
            showView(VIEW_COMPLAINTS);
        });

        adminDashboard.addManageListener(e -> {
            complaintController.refreshList();
            showView(VIEW_COMPLAINTS);
        });
        adminDashboard.addStaffListener(e -> {
            staffController.refreshList();
            showView(VIEW_STAFF);
        });

        if (isDemoMode) {
            mainFrame.setTitle("Smart Campus Complaint & Maintenance System [Demo Mode]");
        }
        mainFrame.showView(VIEW_LOGIN);
        mainFrame.setVisible(true);

        if (isDemoMode) {
            mediator.showNotification("MySQL is offline. Running in Demo Mode with preloaded campus data.", ComplaintMediator.NotificationType.INFO);
        }
    }

    private static void onStudentLogin() {
        User user = authService.getCurrentUser();
        mediator.setCurrentUserId(user.getId());

        mainFrame.buildSidebarForUser(user,
                e -> { dashboardController.refreshDashboard(); showView(VIEW_STUDENT_DASH); },
                e -> { complaintController.refreshList(); showView(VIEW_COMPLAINTS); },
                e -> showView(VIEW_COMPLAINT_FORM),
                null,
                e -> logout());

        mainFrame.setPageTitle("Student Dashboard");
        mainFrame.setUserLabel(user);

        complaintController.refreshList();
        dashboardController.refreshDashboard();
        showView(VIEW_STUDENT_DASH);
    }

    private static void onAdminLogin() {
        User user = authService.getCurrentUser();
        mediator.setCurrentUserId(user.getId());

        mainFrame.buildSidebarForUser(user,
                e -> { dashboardController.refreshDashboard(); showView(VIEW_ADMIN_DASH); },
                e -> { complaintController.refreshList(); showView(VIEW_COMPLAINTS); },
                null,
                e -> { staffController.refreshList(); showView(VIEW_STAFF); },
                e -> logout());

        mainFrame.setPageTitle("Admin Dashboard");
        mainFrame.setUserLabel(user);

        complaintController.refreshList();
        dashboardController.refreshDashboard();
        showView(VIEW_ADMIN_DASH);
    }

    private static void logout() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to log out?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        authService.logout();
        mediator.setCurrentUserId(0);
        mainFrame.setPageTitle("Login");

        mainFrame.buildSidebarForUser(
                new User() {{ setRole(UserRole.STUDENT); }},
                null, null, null, null, null);

        mainFrame.showView(VIEW_LOGIN);
    }

    private static void showView(String name) {
        String title = switch (name) {
            case VIEW_LOGIN            -> "Login";
            case VIEW_STUDENT_DASH     -> "Student Dashboard";
            case VIEW_ADMIN_DASH       -> "Admin Dashboard";
            case VIEW_COMPLAINTS       -> "Complaints";
            case VIEW_COMPLAINT_FORM   -> "Submit Complaint";
            case VIEW_COMPLAINT_DETAIL -> "Complaint Details";
            case VIEW_STAFF            -> "Staff Management";
            default -> "CampusDesk";
        };
        mainFrame.setPageTitle(title);
        mainFrame.showView(name);
    }

    private static void setGlobalFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}
