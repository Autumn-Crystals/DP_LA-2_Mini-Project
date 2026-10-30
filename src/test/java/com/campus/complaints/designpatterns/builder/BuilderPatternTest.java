package com.campus.complaints.designpatterns.builder;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoF Pattern: Builder Pattern Tests")
public class BuilderPatternTest {

    @Test
    @DisplayName("Should successfully construct Complaint when all required fields are provided")
    void testSuccessfulComplaintBuild() {
        Complaint complaint = new ComplaintBuilder()
                .title("Projector Not Working")
                .description("Lab 3 projector lamp is flickering and shutting down.")
                .category(ComplaintCategory.EQUIPMENT)
                .location("Block B, Room 302")
                .priority(Priority.HIGH)
                .reporterId(10)
                .build();

        assertNotNull(complaint);
        assertEquals("Projector Not Working", complaint.getTitle());
        assertEquals("Lab 3 projector lamp is flickering and shutting down.", complaint.getDescription());
        assertEquals(ComplaintCategory.EQUIPMENT, complaint.getCategory());
        assertEquals("Block B, Room 302", complaint.getLocation());
        assertEquals(Priority.HIGH, complaint.getPriority());
        assertEquals(10, complaint.getReporterId());
        assertEquals(ComplaintStatus.PENDING, complaint.getStatus());
        assertNull(complaint.getAssignedStaffId());
        assertNotNull(complaint.getCreatedAt());
        assertNotNull(complaint.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set optional fields correctly via fluent builder API")
    void testOptionalFields() {
        Complaint complaint = new ComplaintBuilder()
                .id(42)
                .title("Wi-Fi down")
                .description("No connection in library 2nd floor.")
                .category(ComplaintCategory.NETWORK)
                .location("Central Library")
                .priority(Priority.CRITICAL)
                .reporterId(5)
                .assignedStaffId(3)
                .status(ComplaintStatus.ASSIGNED)
                .resolutionNotes("Technician dispatched")
                .build();

        assertEquals(42, complaint.getId());
        assertEquals(ComplaintStatus.ASSIGNED, complaint.getStatus());
        assertEquals(Integer.valueOf(3), complaint.getAssignedStaffId());
        assertEquals("Technician dispatched", complaint.getResolutionNotes());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when title is missing or blank")
    void testMissingTitleThrowsException() {
        ComplaintBuilder builder = new ComplaintBuilder()
                .description("Valid description")
                .category(ComplaintCategory.ELECTRICAL)
                .location("Room 101")
                .priority(Priority.MEDIUM)
                .reporterId(1);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when category is null")
    void testMissingCategoryThrowsException() {
        ComplaintBuilder builder = new ComplaintBuilder()
                .title("Valid title")
                .description("Valid description")
                .location("Room 101")
                .priority(Priority.MEDIUM)
                .reporterId(1);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when reporterId is <= 0")
    void testInvalidReporterIdThrowsException() {
        ComplaintBuilder builder = new ComplaintBuilder()
                .title("Valid title")
                .description("Valid description")
                .category(ComplaintCategory.CLEANING)
                .location("Floor 1")
                .priority(Priority.LOW)
                .reporterId(0);

        assertThrows(IllegalStateException.class, builder::build);
    }
}
