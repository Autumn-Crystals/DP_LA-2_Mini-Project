package com.campus.complaints.dao;

import com.campus.complaints.fake.FakeComplaintDAO;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DAO Layer Tests: Complaint Data Access")
public class ComplaintDAOTest {

    private ComplaintDAO complaintDAO;

    @BeforeEach
    void setUp() {
        complaintDAO = new FakeComplaintDAO();
    }

    @Test
    @DisplayName("Should save, find by ID, and update complaint in DAO")
    void testSaveFindAndUpdate() {
        Complaint complaint = new Complaint.Builder()
                .title("Network outage in Lab")
                .description("All workstations lost LAN connection")
                .category(ComplaintCategory.NETWORK)
                .location("Lab 101")
                .priority(Priority.HIGH)
                .reporterId(2)
                .build();

        Complaint saved = complaintDAO.save(complaint);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        Optional<Complaint> found = complaintDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Network outage in Lab", found.get().getTitle());

        saved.setStatus(ComplaintStatus.IN_PROGRESS);
        boolean updated = complaintDAO.update(saved);
        assertTrue(updated);

        assertEquals(ComplaintStatus.IN_PROGRESS, complaintDAO.findById(saved.getId()).get().getStatus());
    }

    @Test
    @DisplayName("Should count complaints by status correctly")
    void testCountByStatus() {
        Complaint c1 = new Complaint.Builder().title("T1").description("D1")
                .category(ComplaintCategory.ELECTRICAL).location("L1").priority(Priority.LOW).reporterId(1).build();
        Complaint c2 = new Complaint.Builder().title("T2").description("D2")
                .category(ComplaintCategory.PLUMBING).location("L2").priority(Priority.MEDIUM).reporterId(1).build();

        complaintDAO.save(c1);
        complaintDAO.save(c2);

        Map<ComplaintStatus, Integer> counts = complaintDAO.countByStatus();
        assertEquals(2, counts.get(ComplaintStatus.PENDING));
    }
}
