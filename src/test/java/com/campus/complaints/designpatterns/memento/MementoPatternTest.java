package com.campus.complaints.designpatterns.memento;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoF Pattern: Memento Pattern Tests")
public class MementoPatternTest {

    private Complaint complaint;
    private ComplaintHistory history;

    @BeforeEach
    void setUp() {
        complaint = new Complaint.Builder()
                .id(55)
                .title("Broken Chair")
                .description("Chair leg broken in Seminar Hall")
                .category(ComplaintCategory.EQUIPMENT)
                .location("Seminar Hall")
                .priority(Priority.LOW)
                .reporterId(7)
                .status(ComplaintStatus.PENDING)
                .assignedStaffId(null)
                .build();

        history = new ComplaintHistory();
    }

    @Test
    @DisplayName("Memento should capture and restore complaint state accurately")
    void testStateCaptureAndRestore() {
        ComplaintMemento memento1 = new ComplaintMemento(
                complaint.getId(),
                complaint.getStatus(),
                complaint.getAssignedStaffId(),
                complaint.getDescription(),
                complaint.getResolutionNotes(),
                1,
                "INITIAL_SUBMISSION"
        );
        history.push(memento1);

        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaint.setAssignedStaffId(12);

        ComplaintMemento memento2 = new ComplaintMemento(
                complaint.getId(),
                complaint.getStatus(),
                complaint.getAssignedStaffId(),
                complaint.getDescription(),
                complaint.getResolutionNotes(),
                1,
                "STAFF_ASSIGNED"
        );
        history.push(memento2);

        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        assertEquals(2, history.size());
        assertTrue(history.canUndo());

        ComplaintMemento popped = history.pop();
        assertNotNull(popped);
        assertEquals("STAFF_ASSIGNED", popped.getActionLabel());
        assertEquals(ComplaintStatus.ASSIGNED, popped.getStatus());
        assertEquals(Integer.valueOf(12), popped.getAssignedStaffId());

        ComplaintMemento initial = history.pop();
        assertNotNull(initial);
        assertEquals("INITIAL_SUBMISSION", initial.getActionLabel());
        assertEquals(ComplaintStatus.PENDING, initial.getStatus());
        assertNull(initial.getAssignedStaffId());

        assertFalse(history.canUndo());
        assertNull(history.pop());
    }

    @Test
    @DisplayName("ComplaintHistory peek should inspect without removing")
    void testPeekDoesNotRemove() {
        ComplaintMemento memento = new ComplaintMemento(
                1, ComplaintStatus.PENDING, null, "Desc", null, 1, "TEST"
        );
        history.push(memento);

        assertEquals(1, history.size());
        ComplaintMemento peeked = history.peek();
        assertNotNull(peeked);
        assertEquals("TEST", peeked.getActionLabel());
        assertEquals(1, history.size(), "Peek must not alter stack size");
    }

    @Test
    @DisplayName("ComplaintHistory clear should reset stack")
    void testClearHistory() {
        history.push(new ComplaintMemento(1, ComplaintStatus.PENDING, null, "D", null, 1, "1"));
        history.push(new ComplaintMemento(1, ComplaintStatus.ASSIGNED, 2, "D", null, 1, "2"));

        assertEquals(2, history.size());
        history.clear();
        assertEquals(0, history.size());
        assertFalse(history.canUndo());
    }
}
