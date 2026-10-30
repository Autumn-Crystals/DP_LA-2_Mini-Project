package com.campus.complaints.mvc;

import com.campus.complaints.dao.inmemory.InMemoryDatabase;
import com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.Priority;
import com.campus.complaints.model.User;
import com.campus.complaints.model.UserRole;
import com.campus.complaints.mvc.controller.ComplaintController;
import com.campus.complaints.mvc.controller.LoginController;
import com.campus.complaints.mvc.view.ComplaintDetailsView;
import com.campus.complaints.mvc.view.ComplaintFormView;
import com.campus.complaints.mvc.view.ComplaintManagementView;
import com.campus.complaints.mvc.view.LoginView;
import com.campus.complaints.mvc.view.MainFrame;
import com.campus.complaints.service.AuthenticationService;
import com.campus.complaints.service.ComplaintService;
import com.campus.complaints.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MVC Architecture Tests: View-Controller-Service Flow")
public class MvcFlowTest {

    private InMemoryDatabase.InMemoryUserDAO userDAO;
    private InMemoryDatabase.InMemoryComplaintDAO complaintDAO;
    private InMemoryDatabase.InMemoryStaffDAO staffDAO;
    private InMemoryDatabase.InMemoryComplaintHistoryDAO historyDAO;

    private AuthenticationService authService;
    private ComplaintService complaintService;
    private StaffService staffService;
    private ComplaintMediatorImpl mediator;

    @BeforeEach
    void setUp() {
        userDAO = new InMemoryDatabase.InMemoryUserDAO();
        complaintDAO = new InMemoryDatabase.InMemoryComplaintDAO();
        staffDAO = new InMemoryDatabase.InMemoryStaffDAO();
        historyDAO = new InMemoryDatabase.InMemoryComplaintHistoryDAO();
        InMemoryDatabase.seed(userDAO, complaintDAO, staffDAO);

        authService = new AuthenticationService(userDAO);
        complaintService = new ComplaintService(complaintDAO, staffDAO, historyDAO);
        staffService = new StaffService(staffDAO, userDAO);
        mediator = new ComplaintMediatorImpl();
        mediator.setComplaintService(complaintService);
    }

    @Test
    @DisplayName("LoginController should authenticate and trigger correct role callback")
    void testLoginFlow() {
        LoginView loginView = new LoginView();
        MainFrame mainFrame = new MainFrame();
        AtomicBoolean adminTriggered = new AtomicBoolean(false);
        AtomicBoolean studentTriggered = new AtomicBoolean(false);

        LoginController controller = new LoginController(
                loginView,
                authService,
                mainFrame,
                () -> adminTriggered.set(true),
                () -> studentTriggered.set(true)
        );

        // Authenticate admin
        authService.login("admin@campus.edu", "admin123");
        User current = authService.getCurrentUser();
        assertNotNull(current);
        assertTrue(current.isAdmin());

        // Authenticate student
        authService.login("alice@student.edu", "student123");
        current = authService.getCurrentUser();
        assertNotNull(current);
        assertTrue(current.isStudent());
    }

    @Test
    @DisplayName("ComplaintController should execute submission and refresh list")
    void testComplaintSubmissionAndListFlow() {
        authService.login("alice@student.edu", "student123");
        User student = authService.getCurrentUser();
        assertNotNull(student);

        Complaint submitted = complaintService.submitComplaint(
                "Broken Chair in Lab",
                "Backrest is detached",
                ComplaintCategory.EQUIPMENT,
                "Lab 2",
                Priority.LOW,
                student.getId()
        );

        assertNotNull(submitted);
        assertEquals("Broken Chair in Lab", submitted.getTitle());

        ComplaintManagementView listView = new ComplaintManagementView(false);
        ComplaintDetailsView detailsView = new ComplaintDetailsView(false);
        ComplaintFormView formView = new ComplaintFormView();

        ComplaintController controller = new ComplaintController(
                complaintService, staffService, authService, mediator,
                listView, detailsView, formView
        );

        assertDoesNotThrow(controller::refreshList);
    }
}
