package com.campus.complaints.designpatterns.template;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoF Pattern: Template Method Pattern Tests")
public class TemplateMethodPatternTest {

    private Complaint generalComplaint;
    private Complaint itComplaint;

    @BeforeEach
    void setUp() {
        generalComplaint = new Complaint.Builder()
                .id(10)
                .title("Water Leak")
                .description("Pipe leaking under sink")
                .category(ComplaintCategory.PLUMBING)
                .location("Washroom 2F")
                .priority(Priority.HIGH)
                .reporterId(1)
                .assignedStaffId(5)
                .status(ComplaintStatus.ASSIGNED)
                .build();

        itComplaint = new Complaint.Builder()
                .id(20)
                .title("Switch Port Inactive")
                .description("Port 12 on switch in Lab 4 has no link")
                .category(ComplaintCategory.NETWORK)
                .location("Computer Lab 4")
                .priority(Priority.CRITICAL)
                .reporterId(2)
                .assignedStaffId(8)
                .status(ComplaintStatus.IN_PROGRESS)
                .build();
    }

    @Test
    @DisplayName("GeneralMaintenanceWorkflow should successfully process complaint when steps valid")
    void testGeneralMaintenanceWorkflowSuccess() throws WorkflowException {
        ComplaintWorkflow workflow = new GeneralMaintenanceWorkflow();

        workflow.processComplaint(generalComplaint, "Replaced gasket and tightened valve.", 100);

        assertEquals(ComplaintStatus.RESOLVED, generalComplaint.getStatus());
        assertEquals("Replaced gasket and tightened valve.", generalComplaint.getResolutionNotes());
        assertNotNull(generalComplaint.getResolvedAt());
    }

    @Test
    @DisplayName("ITSupportWorkflow should enforce technical notes in resolution step")
    void testITSupportWorkflowNotesValidation() {
        ComplaintWorkflow workflow = new ITSupportWorkflow();

        assertThrows(WorkflowException.class, () ->
                workflow.processComplaint(itComplaint, "", 100)
        );

        assertDoesNotThrow(() ->
                workflow.processComplaint(itComplaint, "Port VLAN reconfigured and patched cable.", 100)
        );
        assertEquals(ComplaintStatus.RESOLVED, itComplaint.getStatus());
    }

    @Test
    @DisplayName("Workflow should reject processing when no staff is assigned")
    void testWorkflowRejectsUnassignedComplaint() {
        Complaint unassignedComplaint = new Complaint.Builder()
                .id(30)
                .title("Broken Light")
                .description("Light flickering")
                .category(ComplaintCategory.ELECTRICAL)
                .location("Hallway 1F")
                .priority(Priority.LOW)
                .reporterId(3)
                .build();

        ComplaintWorkflow workflow = new GeneralMaintenanceWorkflow();

        WorkflowException ex = assertThrows(WorkflowException.class, () ->
                workflow.processComplaint(unassignedComplaint, "Done", 100)
        );

        assertTrue(ex.getMessage().contains("No staff assigned"));
    }

    @Test
    @DisplayName("Workflow should reject complaint already RESOLVED or CANCELLED")
    void testWorkflowRejectsAlreadyResolved() {
        generalComplaint.setStatus(ComplaintStatus.RESOLVED);
        ComplaintWorkflow workflow = new GeneralMaintenanceWorkflow();

        assertThrows(WorkflowException.class, () ->
                workflow.processComplaint(generalComplaint, "Notes", 100)
        );
    }
}
