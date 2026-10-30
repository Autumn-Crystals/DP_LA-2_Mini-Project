package com.campus.complaints.service;

import com.campus.complaints.designpatterns.strategy.LeastLoadedStaffStrategy;
import com.campus.complaints.designpatterns.template.WorkflowException;
import com.campus.complaints.fake.FakeComplaintDAO;
import com.campus.complaints.fake.FakeComplaintHistoryDAO;
import com.campus.complaints.fake.FakeStaffDAO;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Service Layer Tests: Integrated Design Patterns")
public class ComplaintServiceTest {

    private FakeComplaintDAO complaintDAO;
    private FakeStaffDAO staffDAO;
    private FakeComplaintHistoryDAO historyDAO;
    private ComplaintService complaintService;

    @BeforeEach
    void setUp() {
        complaintDAO = new FakeComplaintDAO();
        staffDAO = new FakeStaffDAO();
        historyDAO = new FakeComplaintHistoryDAO();

        complaintService = new ComplaintService(complaintDAO, staffDAO, historyDAO);
    }

    @Test
    @DisplayName("submitComplaint uses Builder Pattern to construct and persist complaint")
    void testSubmitComplaint() {
        Complaint result = complaintService.submitComplaint(
                "Elevator Stuck",
                "Elevator in Main Block stuck on 3rd floor",
                ComplaintCategory.EQUIPMENT,
                "Main Block",
                Priority.CRITICAL,
                10
        );

        assertNotNull(result);
        assertTrue(result.getId() > 0);
        assertEquals("Elevator Stuck", result.getTitle());
        assertEquals(ComplaintStatus.PENDING, result.getStatus());
        assertTrue(complaintDAO.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("duplicateComplaint uses Prototype Pattern to clone complaint")
    void testDuplicateComplaint() {
        Complaint original = new Complaint.Builder()
                .title("Broken Projector")
                .description("HDMI port broken")
                .category(ComplaintCategory.EQUIPMENT)
                .location("Room 105")
                .priority(Priority.MEDIUM)
                .reporterId(12)
                .build();
        complaintDAO.save(original);

        Complaint cloned = complaintService.duplicateComplaint(original.getId());

        assertNotNull(cloned);
        assertEquals(0, cloned.getId());
        assertEquals("Broken Projector (Copy)", cloned.getTitle());
        assertEquals(ComplaintStatus.PENDING, cloned.getStatus());
        assertEquals(original.getDescription(), cloned.getDescription());
    }

    @Test
    @DisplayName("autoAssignStaff uses Strategy Pattern to select and assign staff")
    void testAutoAssignStaff() {
        Complaint complaint = new Complaint.Builder()
                .title("Wi-Fi Glitch")
                .description("High packet loss")
                .category(ComplaintCategory.NETWORK)
                .location("Library")
                .priority(Priority.HIGH)
                .reporterId(1)
                .build();
        complaintDAO.save(complaint);

        MaintenanceStaff staff = new MaintenanceStaff(1, 10, "Dave", "dave@campus.edu", "555",
                MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        staff.setActiveComplaintCount(0);
        staffDAO.save(staff);

        complaintService.setAssignmentStrategy(new LeastLoadedStaffStrategy());
        Optional<MaintenanceStaff> assigned = complaintService.autoAssignStaff(complaint, 100);

        assertTrue(assigned.isPresent());
        assertEquals("Dave", assigned.get().getName());
        assertEquals(ComplaintStatus.ASSIGNED, complaint.getStatus());
        assertEquals(Integer.valueOf(1), complaint.getAssignedStaffId());
    }

    @Test
    @DisplayName("resolveComplaint uses Template Method Pattern to validate and resolve complaint")
    void testResolveComplaint() throws WorkflowException {
        Complaint complaint = new Complaint.Builder()
                .title("Water Leak")
                .description("Sink pipe leak")
                .category(ComplaintCategory.PLUMBING)
                .location("Block A")
                .priority(Priority.MEDIUM)
                .reporterId(2)
                .assignedStaffId(3)
                .status(ComplaintStatus.IN_PROGRESS)
                .build();
        complaintDAO.save(complaint);

        complaintService.resolveComplaint(complaint, "Replaced seal rings", 100);

        assertEquals(ComplaintStatus.RESOLVED, complaint.getStatus());
        assertEquals("Replaced seal rings", complaint.getResolutionNotes());
        assertNotNull(complaint.getResolvedAt());
    }

    @Test
    @DisplayName("undoLastChange uses Memento Pattern to roll back status change")
    void testUndoLastChange() {
        Complaint complaint = new Complaint.Builder()
                .title("No Audio in Mic")
                .description("Mic 2 has no audio in Auditorium")
                .category(ComplaintCategory.EQUIPMENT)
                .location("Auditorium")
                .priority(Priority.LOW)
                .reporterId(5)
                .status(ComplaintStatus.PENDING)
                .build();
        complaintDAO.save(complaint);

        complaintService.updateStatus(complaint, ComplaintStatus.ASSIGNED, 100);
        assertEquals(ComplaintStatus.ASSIGNED, complaint.getStatus());

        boolean undoSuccess = complaintService.undoLastChange(complaint, 100);

        assertTrue(undoSuccess);
        assertEquals(ComplaintStatus.PENDING, complaint.getStatus(), "Status must revert to PENDING");
    }
}
