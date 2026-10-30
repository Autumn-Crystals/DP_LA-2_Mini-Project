package com.campus.complaints.designpatterns.prototype;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoF Pattern: Prototype Pattern Tests")
public class PrototypePatternTest {

    @Test
    @DisplayName("Should clone complaint with reset status, new identity, and appended title")
    void testComplaintCloning() {
        Complaint original = new Complaint.Builder()
                .id(101)
                .title("Broken AC in Room 204")
                .description("AC unit makes loud noise and does not cool.")
                .category(ComplaintCategory.ELECTRICAL)
                .location("Block A, Room 204")
                .priority(Priority.HIGH)
                .reporterId(12)
                .status(ComplaintStatus.RESOLVED)
                .assignedStaffId(4)
                .resolutionNotes("Fixed thermostat")
                .build();

        Complaint clone = ComplaintCloner.cloneComplaint(original);

        // 1. New identity and reset fields
        assertEquals(0, clone.getId(), "Clone ID must be reset to 0 (unsaved)");
        assertEquals("Broken AC in Room 204 (Copy)", clone.getTitle());
        assertEquals(original.getDescription(), clone.getDescription());
        assertEquals(original.getCategory(), clone.getCategory());
        assertEquals(original.getLocation(), clone.getLocation());
        assertEquals(original.getPriority(), clone.getPriority());
        assertEquals(original.getReporterId(), clone.getReporterId());

        // 2. Lifecycle states reset
        assertEquals(ComplaintStatus.PENDING, clone.getStatus(), "Clone status must be PENDING");
        assertNull(clone.getAssignedStaffId(), "Clone assigned staff must be null");
        assertNull(clone.getResolvedAt(), "Clone resolvedAt must be null");
        assertNull(clone.getResolutionNotes(), "Clone resolution notes must be null");
        assertNotNull(clone.getCreatedAt());

        // 3. Independence check — mutating clone does not affect original
        clone.setTitle("Different Title");
        clone.setLocation("Different Location");
        clone.setPriority(Priority.LOW);

        assertEquals("Broken AC in Room 204", original.getTitle());
        assertEquals("Block A, Room 204", original.getLocation());
        assertEquals(Priority.HIGH, original.getPriority());
    }
}
