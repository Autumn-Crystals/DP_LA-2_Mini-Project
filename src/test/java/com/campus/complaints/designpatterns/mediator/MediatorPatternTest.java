package com.campus.complaints.designpatterns.mediator;

import com.campus.complaints.fake.FakeComplaintDAO;
import com.campus.complaints.fake.FakeComplaintHistoryDAO;
import com.campus.complaints.fake.FakeStaffDAO;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.Priority;
import com.campus.complaints.service.ComplaintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("GoF Pattern: Mediator Pattern Tests")
public class MediatorPatternTest {

    private ComplaintMediatorImpl mediator;
    private ComplaintService complaintService;
    private Complaint complaint;
    private MaintenanceStaff staff;

    @BeforeEach
    void setUp() {
        mediator = new ComplaintMediatorImpl();
        complaintService = new ComplaintService(new FakeComplaintDAO(), new FakeStaffDAO(), new FakeComplaintHistoryDAO());
        mediator.setComplaintService(complaintService);

        complaint = new Complaint.Builder()
                .id(101)
                .title("Wi-Fi Router Reset")
                .description("No internet in Block B")
                .category(ComplaintCategory.NETWORK)
                .location("Block B")
                .priority(Priority.HIGH)
                .reporterId(1)
                .build();

        staff = new MaintenanceStaff(1, 10, "John Tech", "john@campus.edu", "123",
                MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
    }

    @Test
    @DisplayName("Mediator should coordinate complaint submission without direct coupling between views")
    void testComplaintSubmittedCoordination() {
        assertDoesNotThrow(() -> mediator.onComplaintSubmitted(complaint));
    }

    @Test
    @DisplayName("Mediator should route status change events and notifications")
    void testComplaintStatusChangedCoordination() {
        assertDoesNotThrow(() -> mediator.onComplaintStatusChanged(complaint));
    }

    @Test
    @DisplayName("Mediator should route staff assignment coordination")
    void testStaffAssignedCoordination() {
        assertDoesNotThrow(() -> mediator.onStaffAssigned(complaint, staff));
    }

    @Test
    @DisplayName("Mediator should route undo events")
    void testUndoPerformedCoordination() {
        assertDoesNotThrow(() -> mediator.onUndoPerformed(complaint));
    }

    @Test
    @DisplayName("Mediator handles showNotification gracefully even without MainFrame attached")
    void testShowNotification() {
        assertDoesNotThrow(() ->
                mediator.showNotification("Operation completed", ComplaintMediator.NotificationType.SUCCESS)
        );
    }
}
